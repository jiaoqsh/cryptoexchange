package com.itranswarp.crypto.match;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.order.OrderMessage;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractRunnableService;

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

	// get order from queue:
	final MessageQueue<OrderMessage> orderMessageQueue;

	// send tick to queue:
	final MessageQueue<Tick> tickMessageQueue;

	// send match result to queue:
	final MessageQueue<MatchResult> matchResultMessageQueue;

	// holds order books:
	final OrderBook buyBook;
	final OrderBook sellBook;

	// track market price:
	long marketPrice = 0L;

	// matcher internal status:
	HashStatus hashStatus = new HashStatus();

	public MatchService(@Autowired @Qualifier("orderMessageQueue") MessageQueue<OrderMessage> orderMessageQueue,
			@Autowired @Qualifier("tickMessageQueue") MessageQueue<Tick> tickMessageQueue,
			@Autowired @Qualifier("matchResultMessageQueue") MessageQueue<MatchResult> matchResultMessageQueue) {
		this.orderMessageQueue = orderMessageQueue;
		this.tickMessageQueue = tickMessageQueue;
		this.matchResultMessageQueue = matchResultMessageQueue;
		this.buyBook = new OrderBook(OrderBook.SORT_BUY);
		this.sellBook = new OrderBook(OrderBook.SORT_SELL);
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
			processBuyLimit(order);
			break;
		case SELL_LIMIT:
			processSellLimit(order);
			break;
		case BUY_MARKET:
			throw new RuntimeException("Unsupported type.");
		case SELL_MARKET:
			throw new RuntimeException("Unsupported type.");
		case BUY_CANCEL:
			throw new RuntimeException("Unsupported type.");
		case SELL_CANCEL:
			throw new RuntimeException("Unsupported type.");
		default:
			throw new RuntimeException("Unsupported type.");
		}
	}

	void processBuyLimit(OrderMessage buyTaker) throws InterruptedException {
		MatchResult matchResult = new MatchResult();
		for (;;) {
			OrderMessage sellMaker = this.sellBook.getFirst();
			if (sellMaker == null) {
				// empty order book:
				break;
			}
			if (buyTaker.price < sellMaker.price) {
				break;
			}
			// match with sellMaker.price:
			this.marketPrice = sellMaker.price;
			// max amount to exchange:
			long amount = Math.min(buyTaker.amount, sellMaker.amount);
			buyTaker.amount -= amount;
			sellMaker.amount -= amount;
			notifyTicker(buyTaker.createdAt, this.marketPrice, amount);
			matchResult.addMatchRecord(new MatchRecord(buyTaker.id, sellMaker.id, this.marketPrice, amount));
			updateHashStatus(buyTaker, sellMaker, this.marketPrice, amount);
			if (sellMaker.amount == 0L) {
				this.sellBook.remove(sellMaker);
			}
			if (buyTaker.amount == 0L) {
				buyTaker = null;
				break;
			}
		}
		if (buyTaker != null) {
			this.buyBook.add(buyTaker);
		}
		if (!matchResult.isEmpty()) {
			notifyMatchResult(matchResult);
		}
	}

	void processSellLimit(OrderMessage sellTaker) throws InterruptedException {
		MatchResult matchResult = new MatchResult();
		for (;;) {
			OrderMessage buyMaker = this.buyBook.getFirst();
			if (buyMaker == null) {
				// empty order book:
				break;
			}
			if (sellTaker.price > buyMaker.price) {
				break;
			}
			// match with buyMaker.price:
			this.marketPrice = buyMaker.price;
			// max amount to match:
			long amount = Math.min(sellTaker.amount, buyMaker.amount);
			sellTaker.amount -= amount;
			buyMaker.amount -= amount;
			notifyTicker(sellTaker.createdAt, this.marketPrice, amount);
			matchResult.addMatchRecord(new MatchRecord(sellTaker.id, buyMaker.id, this.marketPrice, amount));
			updateHashStatus(sellTaker, buyMaker, this.marketPrice, amount);
			if (buyMaker.amount == 0L) {
				this.buyBook.remove(buyMaker);
			}
			if (sellTaker.amount == 0L) {
				sellTaker = null;
				break;
			}
		}
		if (sellTaker != null) {
			this.sellBook.add(sellTaker);
		}
		if (!matchResult.isEmpty()) {
			notifyMatchResult(matchResult);
		}
	}

	void notifyTicker(long time, long price, long amount) throws InterruptedException {
		Tick tick = new Tick(time, price, amount);
		this.tickMessageQueue.sendMessage(tick);
	}

	void notifyMatchResult(MatchResult matchResult) throws InterruptedException {
		this.matchResultMessageQueue.sendMessage(matchResult);
	}

	private final ByteBuffer hashBuffer = ByteBuffer.allocate(Long.BYTES * 4 + Integer.BYTES * 2);

	private void updateHashStatus(OrderMessage taker, OrderMessage maker, long price, long amount) {
		hashBuffer.clear();
		hashBuffer.putLong(taker.id);
		hashBuffer.putInt(taker.type.value);
		hashBuffer.putLong(maker.id);
		hashBuffer.putInt(maker.type.value);
		hashBuffer.putLong(price);
		hashBuffer.putLong(amount);
		this.hashStatus.updateStatus(hashBuffer);
	}

	public byte[] getHashStatus() {
		return this.hashStatus.getStatus();
	}

	public void dump() {
		System.out.println(String.format("S: %5d more", this.sellBook.size()));
		this.sellBook.dump(true);
		System.out.println(String.format("P: $%4d ----------------", this.marketPrice));
		this.buyBook.dump(false);
		System.out.println(String.format("B: %5d more", this.buyBook.size()));
		System.out.println(String.format("%032x\n", new BigInteger(1, this.hashStatus.getStatus())));
	}

}
