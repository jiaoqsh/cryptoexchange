package com.itranswarp.crypto.match;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.match.message.CancelledMessage;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.match.message.MatchResultMessage;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.sequence.message.CancelOrderMessage;
import com.itranswarp.crypto.sequence.message.LimitOrderMessage;
import com.itranswarp.crypto.sequence.message.MarketOrderMessage;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.crypto.store.AbstractRunnableService;
import com.itranswarp.crypto.symbol.Symbol;

/**
 * Match engine is single-threaded service to process order messages and
 * produces match results, depth and tick messages.
 * 
 * Match engine is stateless: same input sequence always produces same match
 * results sequence.
 * 
 * @author liaoxuefeng
 */
@Component
public class MatchService extends AbstractRunnableService {

	static final BigDecimal PRICE_DELTA = new BigDecimal("0.01");

	final Symbol symbol = Symbol.BTC_USD;

	// get sequenced-order from queue:
	@Autowired
	@Qualifier("orderMessageQueue")
	MessageQueue<OrderMessage> orderMessageQueue;

	// send tick to queue:
	@Autowired
	@Qualifier("tickMessageQueue")
	MessageQueue<TickMessage> tickMessageQueue;

	// send match result to queue:
	@Autowired
	@Qualifier("matchMessageQueue")
	MessageQueue<MatchMessage> matchMessageQueue;

	// holds order books:
	final OrderBook buyBook = new OrderBook(OrderDirection.BUY);
	final OrderBook sellBook = new OrderBook(OrderDirection.SELL);

	// track market price:
	BigDecimal marketPrice = BigDecimal.ZERO;

	// matcher internal status:
	HashStatus hashStatus = new HashStatus();

	// depth snapshot:
	DepthSnapshot depthSnapshot = new DepthSnapshot(0, Collections.emptyList(), Collections.emptyList());

	public MatchService() {
	}

	/**
	 * Get depth snapshot. This method is thread-safe.
	 * 
	 * @return A DepthSnapshot object.
	 */
	public DepthSnapshot getDepthSnapshot() {
		return this.depthSnapshot;
	}

	@Override
	protected void process() throws InterruptedException {
		while (true) {
			OrderMessage order = orderMessageQueue.getMessage();
			processOrder(order);
		}
	}

	@Override
	protected void clean() throws InterruptedException {
		while (true) {
			OrderMessage order = orderMessageQueue.getMessage(10);
			if (order != null) {
				processOrder(order);
			} else {
				break;
			}
		}
	}

	/**
	 * Process an order.
	 * 
	 * @param order
	 *            Order object.
	 */
	void processOrder(OrderMessage message) throws InterruptedException {
		if (message instanceof LimitOrderMessage) {
			// process limit order:
			LimitOrderMessage limitOrderMessage = (LimitOrderMessage) message;
			OrderItem lItem = new OrderItem(limitOrderMessage.direction, limitOrderMessage.seqId,
					limitOrderMessage.orderId, limitOrderMessage.price, limitOrderMessage.amount,
					limitOrderMessage.createdAt);
			OrderBook lMakerBook = limitOrderMessage.direction == OrderDirection.BUY ? this.sellBook : this.buyBook;
			OrderBook lTakerBook = limitOrderMessage.direction == OrderDirection.BUY ? this.buyBook : this.sellBook;
			doLimitOrder(lItem, lMakerBook, lTakerBook);
		} else if (message instanceof MarketOrderMessage) {
			// process market order:
			MarketOrderMessage marketOrderMessage = (MarketOrderMessage) message;
			OrderItem mItem = new OrderItem(marketOrderMessage.direction, marketOrderMessage.seqId,
					marketOrderMessage.orderId, null, marketOrderMessage.amount, marketOrderMessage.createdAt);
			OrderBook mMakerBook = marketOrderMessage.direction == OrderDirection.BUY ? this.sellBook : this.buyBook;
			OrderBook mTakerBook = marketOrderMessage.direction == OrderDirection.BUY ? this.buyBook : this.sellBook;
			doMarketOrder(mItem, mMakerBook, mTakerBook);
		} else if (message instanceof CancelOrderMessage) {
			// process cancel order:
			CancelOrderMessage cancelOrderMessage = (CancelOrderMessage) message;
			OrderBook cancelInBook = cancelOrderMessage.direction == OrderDirection.BUY ? this.buyBook : this.sellBook;
			doCancelOrder(cancelOrderMessage, cancelInBook);
		} else {
			throw new RuntimeException("Unsupported type of message: " + message.getClass().getName());
		}
	}

	void doLimitOrder(OrderItem taker, OrderBook makerBook, OrderBook takerBook) throws InterruptedException {
		final long ts = taker.createdAt;
		MatchResultMessage matchResult = new MatchResultMessage(taker.orderId, ts);
		for (;;) {
			OrderItem maker = makerBook.getFirst();
			if (maker == null) {
				// empty order book:
				break;
			}
			if (taker.direction == OrderDirection.BUY && taker.price.compareTo(maker.price) < 0) {
				break;
			} else if (taker.direction == OrderDirection.SELL && taker.price.compareTo(maker.price) > 0) {
				break;
			}
			// match with maker.price:
			this.marketPrice = maker.price;
			// max amount to exchange:
			BigDecimal amount = taker.amount.min(maker.amount);
			taker.amount = taker.amount.subtract(amount);
			maker.amount = maker.amount.subtract(amount);
			// is maker fully filled?
			OrderStatus makerStatus = maker.amount.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.FULLY_FILLED
					: OrderStatus.PARTIAL_FILLED;
			notifyTick(this.symbol, ts, this.marketPrice, amount);
			matchResult.addMatchRecord(
					new MatchRecordMessage(taker.orderId, maker.orderId, this.marketPrice, amount, makerStatus));
			updateHashStatus(taker, maker, this.marketPrice, amount);
			// should remove maker from order book?
			if (makerStatus == OrderStatus.FULLY_FILLED) {
				makerBook.remove(maker);
			}
			// should remove taker from order book?
			if (taker.amount.compareTo(BigDecimal.ZERO) == 0) {
				taker = null;
				break;
			}
		}
		if (taker != null) {
			takerBook.add(taker);
		}
		if (!matchResult.isEmpty()) {
			matchResult.takerStatus = taker == null ? OrderStatus.FULLY_FILLED : OrderStatus.PARTIAL_FILLED;
			notifyMatchResult(matchResult);
		}
		updateDepthSnapshot(ts);
	}

	void doMarketOrder(OrderItem taker, OrderBook makerBook, OrderBook takerBook) throws InterruptedException {
		final long ts = taker.createdAt;
		final int scale = this.symbol.base.getScale();
		MatchResultMessage matchResult = new MatchResultMessage(taker.orderId, ts);
		for (;;) {
			OrderItem maker = makerBook.getFirst();
			if (maker == null) {
				// empty order book:
				break;
			}
			if (taker.direction == OrderDirection.BUY) {
				// max amount to exchange:
				BigDecimal amount = taker.amount.divide(maker.price, scale, RoundingMode.DOWN);
				if (amount.signum() == 0) {
					// amount = 0:
					break;
				}
				amount = amount.min(maker.amount);
				// match with maker.price:
				this.marketPrice = maker.price;
				// how much spend:
				BigDecimal spend = maker.price.multiply(amount);
				taker.amount = taker.amount.subtract(spend);
				maker.amount = maker.amount.subtract(amount);
				// is maker fully filled?
				OrderStatus makerStatus = maker.amount.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.FULLY_FILLED
						: OrderStatus.PARTIAL_FILLED;
				notifyTick(this.symbol, ts, this.marketPrice, amount);
				matchResult.addMatchRecord(
						new MatchRecordMessage(taker.orderId, maker.orderId, this.marketPrice, amount, makerStatus));
				updateHashStatus(taker, maker, this.marketPrice, amount);
				// should remove maker from order book?
				if (makerStatus == OrderStatus.FULLY_FILLED) {
					makerBook.remove(maker);
				}
			} else {
				// taker.direction==OrderDirection.SELL:
				// max amount to exchange:
				BigDecimal amount = taker.amount.min(maker.amount);
				if (amount.signum() == 0) {
					break;
				}
				// match with maker.price:
				this.marketPrice = maker.price;
				taker.amount = taker.amount.subtract(amount);
				maker.amount = maker.amount.subtract(amount);
				// is maker fully filled?
				OrderStatus makerStatus = maker.amount.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.FULLY_FILLED
						: OrderStatus.PARTIAL_FILLED;
				notifyTick(this.symbol, ts, this.marketPrice, amount);
				matchResult.addMatchRecord(
						new MatchRecordMessage(taker.orderId, maker.orderId, this.marketPrice, amount, makerStatus));
				updateHashStatus(taker, maker, this.marketPrice, amount);
				// should remove maker from order book?
				if (makerStatus == OrderStatus.FULLY_FILLED) {
					makerBook.remove(maker);
				}
			}
		}
		// how much left:
		boolean hasMatchRecord = matchResult.hasMatchRecord();
		matchResult.takerStatus = hasMatchRecord ? OrderStatus.FULLY_FILLED : OrderStatus.FULLY_CANCELLED;
		matchResult.takerAmount = taker.amount;
		notifyMatchResult(matchResult);
		updateDepthSnapshot(ts);
	}

	void doCancelOrder(CancelOrderMessage order, OrderBook orderBook) throws InterruptedException {
		OrderItem msg = new OrderItem(order.direction, order.refSeqId, order.refOrderId, order.price,
				/* ignore amount */ null, order.createdAt);
		OrderItem origin = orderBook.remove(msg);
		if (origin == null) {
			// WARNING: no such order item exist:
		} else {
			updateHashStatus(msg);
		}
		MatchMessage message = new CancelledMessage(origin != null, order.orderId, order.refOrderId, origin.amount,
				order.createdAt);
		notifyMatchResult(message);
	}

	/**
	 * Update depth snapshot. This method MUST be only invoked in MatchService's
	 * process-thread.
	 */
	void updateDepthSnapshot(long timestamp) {
		List<OrderSnapshot> buyOrders = this.buyBook.getSnapshot(10);
		List<OrderSnapshot> sellOrders = this.sellBook.getSnapshot(10);
		this.depthSnapshot = new DepthSnapshot(timestamp, buyOrders, sellOrders);
	}

	void notifyTick(Symbol symbol, long time, BigDecimal price, BigDecimal amount) throws InterruptedException {
		TickMessage tick = new TickMessage(symbol, time, price, amount);
		this.tickMessageQueue.sendMessage(tick);
	}

	void notifyMatchResult(MatchMessage matchMessage) throws InterruptedException {
		this.matchMessageQueue.sendMessage(matchMessage);
	}

	private final ByteBuffer hashBuffer = ByteBuffer.allocate(128);

	private void updateHashStatus(OrderItem order) {
		hashBuffer.clear();
		hashBuffer.putInt(order.direction.value);
		hashBuffer.putLong(order.orderId);
		hashBuffer.putLong(order.seqId);
		this.hashStatus.updateStatus(hashBuffer);
	}

	private void updateHashStatus(OrderItem taker, OrderItem maker, BigDecimal price, BigDecimal amount) {
		hashBuffer.clear();
		hashBuffer.putInt(taker.direction.value);
		hashBuffer.putLong(taker.orderId);
		hashBuffer.putLong(taker.seqId);
		hashBuffer.putInt(maker.direction.value);
		hashBuffer.putLong(maker.orderId);
		hashBuffer.putLong(maker.seqId);
		hashBuffer.put(price.toString().getBytes(StandardCharsets.UTF_8));
		hashBuffer.put(amount.toString().getBytes(StandardCharsets.UTF_8));
		this.hashStatus.updateStatus(hashBuffer);
	}

	public byte[] getHashStatus() {
		return this.hashStatus.getStatus();
	}

	public void dump() {
		System.out.println(String.format("S: %5d more", this.sellBook.size()));
		this.sellBook.dump(true);
		System.out.println(String.format("P: $%.4f ----------------", this.marketPrice));
		this.buyBook.dump(false);
		System.out.println(String.format("B: %5d more", this.buyBook.size()));
		System.out.println(String.format("%032x\n", new BigInteger(1, this.hashStatus.getStatus())));
	}

}
