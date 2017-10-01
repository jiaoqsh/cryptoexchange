package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.itranswarp.crypto.RunnableResource;
import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.sequence.message.LimitOrderMessage;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.crypto.symbol.Symbol;

public class MatchServiceDupTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDuplicateEngine() throws Exception {
		MessageQueue<OrderMessage> orderQueue1 = new MessageQueue<>(10000);
		MessageQueue<OrderMessage> orderQueue2 = new MessageQueue<>(10000);
		MessageQueue<TickMessage> tickQueue1 = new MessageQueue<>(1000);
		MessageQueue<TickMessage> tickQueue2 = new MessageQueue<>(1000);
		MessageQueue<MatchMessage> matchQueue1 = new MessageQueue<>(1000000);
		MessageQueue<MatchMessage> matchQueue2 = new MessageQueue<>(1000000);

		TickHandler tickHandler1 = new TickHandler(tickQueue1);
		TickHandler tickHandler2 = new TickHandler(tickQueue2);

		tickHandler1.start();
		tickHandler2.start();

		MatchService matcher1 = newMatchService(orderQueue1, tickQueue1, matchQueue1);
		MatchService matcher2 = newMatchService(orderQueue2, tickQueue2, matchQueue2);

		matcher1.start();
		matcher2.start();

		long basePrice = 5000;
		orderQueue1.sendMessage(createOrder(OrderType.BUY_LIMIT, 1, basePrice, 1));
		orderQueue2.sendMessage(createOrder(OrderType.BUY_LIMIT, 1, basePrice, 1));
		orderQueue1.sendMessage(createOrder(OrderType.SELL_LIMIT, 2, basePrice, 1));
		orderQueue2.sendMessage(createOrder(OrderType.SELL_LIMIT, 2, basePrice, 1));
		final long NUM = 1000000;
		long totalAmount = 0L;
		long startTime = System.currentTimeMillis();
		for (long id = 10; id < NUM; id++) {
			boolean buy = random.nextInt() % 2 == 0;
			long price = 2000 + (buy ? randomLong(-1000, 500) : randomLong(-500, 1000));
			long amount = 1 + randomLong(1, 500) / (NUM >> 17);
			totalAmount += amount;
			orderQueue1.sendMessage(createOrder(buy ? OrderType.BUY_LIMIT : OrderType.SELL_LIMIT, id, price, amount));
			orderQueue2.sendMessage(createOrder(buy ? OrderType.BUY_LIMIT : OrderType.SELL_LIMIT, id, price, amount));
			if ((id % 10000L) == 0L) {
				System.out.print('.');
				Thread.sleep(1);
			}
		}
		System.out.println();
		Thread.sleep(1000);
		// shutdown:
		matcher1.shutdown();
		matcher2.shutdown();

		tickHandler1.shutdown();
		tickHandler2.shutdown();

		// make sure orderQueue is empty:
		orderQueue1.shutdown();
		orderQueue2.shutdown();

		long endTime = System.currentTimeMillis();
		matcher1.dump();
		matcher2.dump();
		System.out.println("1M orders takes time: " + (endTime - startTime));
		System.out.println("\nTickers 1:\n" + tickHandler1.getCount());
		System.out.println("\nTickers 2:\n" + tickHandler2.getCount());
		assertArrayEquals(matcher1.getHashStatus(), matcher2.getHashStatus());
		assertEquals(tickHandler1.getCount(), tickHandler2.getCount());
		assertArrayEquals(tickHandler1.getStatus(), tickHandler2.getStatus());
		System.out.println("Place orders: " + totalAmount);
		System.out.println("Total tickers: " + tickQueue1.totalMessages());
		System.out.println("Total tickers: " + tickQueue2.totalMessages());
	}

	static MatchService newMatchService(MessageQueue<OrderMessage> orderMessageQueue,
			MessageQueue<TickMessage> tickMessageQueue, MessageQueue<MatchMessage> matchMessageQueue) {
		MatchService matchService = new MatchService();
		matchService.orderMessageQueue = orderMessageQueue;
		matchService.tickMessageQueue = tickMessageQueue;
		matchService.matchMessageQueue = matchMessageQueue;
		return matchService;
	}

	static OrderMessage createOrder(OrderType type, long id, long price, long amount) {
		Order o = new Order();
		o.type = type;
		o.id = id;
		o.seqId = o.id;
		o.userId = 11100;
		o.price = BigDecimal.valueOf(price);
		o.amount = BigDecimal.valueOf(amount);
		o.createdAt = System.currentTimeMillis();
		return new LimitOrderMessage(Symbol.BTC_USD,
				type == OrderType.BUY_LIMIT ? OrderDirection.BUY : OrderDirection.SELL, o.userId, o.seqId, o.id,
				o.createdAt, o.price, o.amount);
	}

	static final Random random = new Random();

	static long randomLong(int min, int max) {
		return random.nextInt(max - min) + min;
	}

}

class TickHandler implements RunnableResource {

	final MessageQueue<TickMessage> tickQueue;
	int count = 0;
	HashStatus status = new HashStatus();
	Thread thread;

	public TickHandler(MessageQueue<TickMessage> tickQueue) {
		this.tickQueue = tickQueue;
	}

	public synchronized void start() {
		thread = new Thread(() -> {
			try {
				while (true) {
					TickMessage tick = tickQueue.getMessage();
					processTick(tick);
				}
			} catch (InterruptedException e) {
			}
			try {
				while (true) {
					TickMessage tick = tickQueue.getMessage(10);
					if (tick != null) {
						processTick(tick);
					} else {
						break;
					}
				}
			} catch (InterruptedException e) {
			}
		});
		thread.start();
	}

	public synchronized void shutdown() {
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}

	void processTick(TickMessage tick) {
		count++;
		status.updateStatus(tick.toString());
	}

	public int getCount() {
		return this.count;
	}

	public byte[] getStatus() {
		return this.status.getStatus();
	}
}
