package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.junit.Test;

import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.order.OrderMessage;
import com.itranswarp.crypto.order.OrderType;

public class OrderBookTest {

	@Test
	public void testGetSnapshot() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFirst() {
		fail("Not yet implemented");
	}

	public static void main(String[] args) {
		// sell:
		OrderBook sell = new OrderBook(OrderBook.SORT_SELL);
		for (long id = 1; id < 20; id++) {
			OrderMessage o = createOrder(OrderType.SELL_LIMIT, id, randomDecimal(110, 130), randomDecimal(1, 100));
			sell.add(o);
		}
		sell.dump(true);
		System.out.println("------------------------------");
		// buy:
		OrderBook buy = new OrderBook(OrderBook.SORT_BUY);
		for (long id = 100; id < 120; id++) {
			OrderMessage o = createOrder(OrderType.BUY_LIMIT, id, randomDecimal(100, 120), randomDecimal(1, 100));
			buy.add(o);
		}
		buy.dump(false);
	}

	static OrderMessage createOrder(OrderType type, long id, BigDecimal price, BigDecimal amount) {
		Order o = new Order();
		o.type = type;
		o.id = id;
		o.price = price;
		o.amount = amount;
		return new OrderMessage(o);
	}

	static final Random random = new Random();

	static BigDecimal randomDecimal(int min, int max) {
		return new BigDecimal(random.nextInt(max - min) + min).divide(BigDecimal.TEN, 2, RoundingMode.DOWN);
	}

}
