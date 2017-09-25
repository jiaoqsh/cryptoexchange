package com.itranswarp.crypto.match;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.sequence.OrderMessage;
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

	// get order from queue:
	final MessageQueue<OrderMessage> orderMessageQueue;

	// send tick to queue:
	final MessageQueue<TickMessage> tickMessageQueue;

	// send match result to queue:
	final MessageQueue<MatchResult> matchResultMessageQueue;

	// holds order books:
	final OrderBook buyBook;
	final OrderBook sellBook;

	// track market price:
	BigDecimal marketPrice = BigDecimal.ZERO;

	// matcher internal status:
	HashStatus hashStatus = new HashStatus();

	// depth snapshot:
	DepthSnapshot depthSnapshot = new DepthSnapshot(0, Collections.emptyList(), Collections.emptyList());

	public MatchService(@Autowired @Qualifier("orderMessageQueue") MessageQueue<OrderMessage> orderMessageQueue,
			@Autowired @Qualifier("tickMessageQueue") MessageQueue<TickMessage> tickMessageQueue,
			@Autowired @Qualifier("matchResultMessageQueue") MessageQueue<MatchResult> matchResultMessageQueue) {
		this.orderMessageQueue = orderMessageQueue;
		this.tickMessageQueue = tickMessageQueue;
		this.matchResultMessageQueue = matchResultMessageQueue;
		this.buyBook = new OrderBook(OrderBook.OrderBookType.BUY);
		this.sellBook = new OrderBook(OrderBook.OrderBookType.SELL);
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
	void processOrder(OrderMessage order) throws InterruptedException {
		switch (order.type) {
		case BUY_LIMIT:
			processLimitOrder(order, order.type, this.sellBook);
			break;
		case SELL_LIMIT:
			processLimitOrder(order, order.type, this.buyBook);
			break;
		case BUY_CANCEL:
		case SELL_CANCEL:
		case BUY_MARKET:
		case SELL_MARKET:
		default:
			throw new RuntimeException("Unsupported type.");
		}
	}

	void processLimitOrder(OrderMessage taker, OrderType orderType, OrderBook orderBook) throws InterruptedException {
		final long ts = taker.createdAt;
		MatchResult matchResult = new MatchResult(ts);
		for (;;) {
			OrderMessage maker = orderBook.getFirst();
			if (maker == null) {
				// empty order book:
				break;
			}
			if (orderType == OrderType.BUY_LIMIT && taker.price.compareTo(maker.price) < 0) {
				break;
			} else if (orderType == OrderType.SELL_LIMIT && taker.price.compareTo(maker.price) > 0) {
				break;
			}
			// match with sellMaker.price:
			this.marketPrice = maker.price;
			// max amount to exchange:
			BigDecimal amount = taker.amount.min(maker.amount);
			taker.amount = taker.amount.subtract(amount);
			maker.amount = maker.amount.subtract(amount);
			// is maker fully filled?
			OrderStatus makerStatus = maker.amount.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.FULLY_FILLED
					: OrderStatus.PARTIAL_FILLED;
			notifyTick(taker.symbol, taker.createdAt, this.marketPrice, amount);
			matchResult.addMatchRecord(new MatchRecord(taker.id, maker.id, this.marketPrice, amount, makerStatus));
			updateHashStatus(taker, maker, this.marketPrice, amount);
			// should remove maker from order book?
			if (makerStatus == OrderStatus.FULLY_FILLED) {
				orderBook.remove(maker);
			}
			// should remove taker from order book?
			if (taker.amount.compareTo(BigDecimal.ZERO) == 0) {
				taker = null;
				break;
			}
		}
		if (taker != null) {
			orderBook.add(taker);
		}
		if (!matchResult.isEmpty()) {
			matchResult.takerStatus = taker == null ? OrderStatus.FULLY_FILLED : OrderStatus.PARTIAL_FILLED;
			notifyMatchResult(matchResult);
		}
		updateDepthSnapshot(ts);
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

	void notifyMatchResult(MatchResult matchResult) throws InterruptedException {
		this.matchResultMessageQueue.sendMessage(matchResult);
	}

	private final ByteBuffer hashBuffer = ByteBuffer.allocate(128);

	private void updateHashStatus(OrderMessage taker, OrderMessage maker, BigDecimal price, BigDecimal amount) {
		hashBuffer.clear();
		hashBuffer.putLong(taker.id);
		hashBuffer.putInt(taker.type.value);
		hashBuffer.putLong(maker.id);
		hashBuffer.putInt(maker.type.value);
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
