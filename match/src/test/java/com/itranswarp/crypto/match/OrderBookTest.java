package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.junit.Test;

import com.itranswarp.crypto.sequence.OrderDirection;

public class OrderBookTest {

	@Test
	public void testGetOrderItem() {
		OrderBook sell = new OrderBook(OrderDirection.SELL);
		sell.add(createOrder(OrderDirection.SELL, 1, "19.05", "0.2"));
		OrderItem key = createOrder(OrderDirection.SELL, 1, "19.05", "0");
		// get original item should still be 0.2:
		OrderItem value = sell.remove(key);
		assertEquals(value.amount, new BigDecimal("0.2"));
	}

	@Test
	public void testGetSellBookFirst() {
		OrderBook sell = new OrderBook(OrderDirection.SELL);
		assertNull(sell.getFirst());
		// 19.05 id=1,7
		// 18.87 id=4,5
		// 17.35 id=2
		// 15.41 id=3,6
		// ---------------- price
		sell.add(createOrder(OrderDirection.SELL, 1, "19.05", "0.1"));
		sell.add(createOrder(OrderDirection.SELL, 2, "17.35", "0.2"));
		sell.add(createOrder(OrderDirection.SELL, 3, "15.41", "0.3"));
		sell.add(createOrder(OrderDirection.SELL, 4, "18.87", "0.4"));
		sell.add(createOrder(OrderDirection.SELL, 5, "18.87", "0.5"));
		sell.add(createOrder(OrderDirection.SELL, 6, "15.41", "0.6"));
		sell.add(createOrder(OrderDirection.SELL, 7, "19.05", "0.7"));
		OrderItem m = sell.getFirst();
		assertNotNull(m);
		assertEquals(3, m.orderId);
		assertTrue(m.price.compareTo(new BigDecimal("15.41")) == 0);
		assertTrue(m.amount.compareTo(new BigDecimal("0.3")) == 0);
	}

	@Test
	public void testGetBuyBookFirst() {
		OrderBook buy = new OrderBook(OrderDirection.BUY);
		assertNull(buy.getFirst());
		// ---------------- price
		// 19.05 id=1,7
		// 18.87 id=4,5
		// 17.35 id=2
		// 15.41 id=3,6
		buy.add(createOrder(OrderDirection.BUY, 1, "19.05", "0.1"));
		buy.add(createOrder(OrderDirection.BUY, 2, "17.35", "0.2"));
		buy.add(createOrder(OrderDirection.BUY, 3, "15.41", "0.3"));
		buy.add(createOrder(OrderDirection.BUY, 4, "18.87", "0.4"));
		buy.add(createOrder(OrderDirection.BUY, 5, "18.87", "0.5"));
		buy.add(createOrder(OrderDirection.BUY, 6, "15.41", "0.6"));
		buy.add(createOrder(OrderDirection.BUY, 7, "19.05", "0.7"));
		OrderItem m = buy.getFirst();
		assertNotNull(m);
		assertEquals(1, m.orderId);
		assertTrue(m.price.compareTo(new BigDecimal("19.05")) == 0);
		assertTrue(m.amount.compareTo(new BigDecimal("0.1")) == 0);
	}

	public static void main(String[] args) {
		// sell:
		OrderBook sell = new OrderBook(OrderDirection.SELL);
		for (long id = 1; id < 20; id++) {
			OrderItem o = createOrder(OrderDirection.SELL, id, randomDecimal(110, 130), randomDecimal(1, 100));
			sell.add(o);
		}
		sell.dump(true);
		System.out.println("--------------------------------------------------");
		// buy:
		OrderBook buy = new OrderBook(OrderDirection.BUY);
		for (long id = 100; id < 120; id++) {
			OrderItem o = createOrder(OrderDirection.BUY, id, randomDecimal(100, 120), randomDecimal(1, 100));
			buy.add(o);
		}
		buy.dump(false);
	}

	static OrderItem createOrder(OrderDirection direction, long id, String price, String amount) {
		return new OrderItem(direction, id + 1, id, new BigDecimal(price), new BigDecimal(amount),
				System.currentTimeMillis());
	}

	static final Random random = new Random();

	static String randomDecimal(int min, int max) {
		return "" + new BigDecimal(random.nextInt(max - min) + min).divide(BigDecimal.TEN, 2, RoundingMode.DOWN);
	}

}
