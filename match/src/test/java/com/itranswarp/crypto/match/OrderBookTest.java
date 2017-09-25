package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.junit.Test;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.sequence.OrderMessage;

public class OrderBookTest {

	@Test
	public void testGetSnapshot() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSellBookFirst() {
		OrderBook sell = new OrderBook(OrderBook.OrderBookType.SELL);
		assertNull(sell.getFirst());
		// 19.05 id=1,7
		// 18.87 id=4,5
		// 17.35 id=2
		// 15.41 id=3,6
		// ---------------- price
		sell.add(createOrder(OrderType.SELL_LIMIT, 1, "19.05", "0.1"));
		sell.add(createOrder(OrderType.SELL_LIMIT, 2, "17.35", "0.2"));
		sell.add(createOrder(OrderType.SELL_LIMIT, 3, "15.41", "0.3"));
		sell.add(createOrder(OrderType.SELL_LIMIT, 4, "18.87", "0.4"));
		sell.add(createOrder(OrderType.SELL_LIMIT, 5, "18.87", "0.5"));
		sell.add(createOrder(OrderType.SELL_LIMIT, 6, "15.41", "0.6"));
		sell.add(createOrder(OrderType.SELL_LIMIT, 7, "19.05", "0.7"));
		OrderMessage m = sell.getFirst();
		assertNotNull(m);
		assertEquals(3, m.id);
		assertTrue(m.price.compareTo(new BigDecimal("15.41")) == 0);
		assertTrue(m.amount.compareTo(new BigDecimal("0.3")) == 0);
	}

	@Test
	public void testGetBuyBookFirst() {
		OrderBook buy = new OrderBook(OrderBook.OrderBookType.BUY);
		assertNull(buy.getFirst());
		// ---------------- price
		// 19.05 id=1,7
		// 18.87 id=4,5
		// 17.35 id=2
		// 15.41 id=3,6
		buy.add(createOrder(OrderType.BUY_LIMIT, 1, "19.05", "0.1"));
		buy.add(createOrder(OrderType.BUY_LIMIT, 2, "17.35", "0.2"));
		buy.add(createOrder(OrderType.BUY_LIMIT, 3, "15.41", "0.3"));
		buy.add(createOrder(OrderType.BUY_LIMIT, 4, "18.87", "0.4"));
		buy.add(createOrder(OrderType.BUY_LIMIT, 5, "18.87", "0.5"));
		buy.add(createOrder(OrderType.BUY_LIMIT, 6, "15.41", "0.6"));
		buy.add(createOrder(OrderType.BUY_LIMIT, 7, "19.05", "0.7"));
		OrderMessage m = buy.getFirst();
		assertNotNull(m);
		assertEquals(1, m.id);
		assertTrue(m.price.compareTo(new BigDecimal("19.05")) == 0);
		assertTrue(m.amount.compareTo(new BigDecimal("0.1")) == 0);
	}

	public static void main(String[] args) {
		// sell:
		OrderBook sell = new OrderBook(OrderBook.OrderBookType.SELL);
		for (long id = 1; id < 20; id++) {
			OrderMessage o = createOrder(OrderType.SELL_LIMIT, id, randomDecimal(110, 130), randomDecimal(1, 100));
			sell.add(o);
		}
		sell.dump(true);
		System.out.println("------------------------------");
		// buy:
		OrderBook buy = new OrderBook(OrderBook.OrderBookType.BUY);
		for (long id = 100; id < 120; id++) {
			OrderMessage o = createOrder(OrderType.BUY_LIMIT, id, randomDecimal(100, 120), randomDecimal(1, 100));
			buy.add(o);
		}
		buy.dump(false);
	}

	static OrderMessage createOrder(OrderType type, long id, String price, String amount) {
		Order o = new Order();
		o.type = type;
		o.id = id;
		o.price = new BigDecimal(price);
		o.amount = new BigDecimal(amount);
		return new OrderMessage(id, o);
	}

	static final Random random = new Random();

	static String randomDecimal(int min, int max) {
		return "" + new BigDecimal(random.nextInt(max - min) + min).divide(BigDecimal.TEN, 2, RoundingMode.DOWN);
	}

}
