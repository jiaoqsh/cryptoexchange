package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.match.OrderBook;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.sequence.OrderMessage;

public class OrderMessageInTreeSetTest {

	@Test
	public void testOrderInTreeSet() {
		Set<OrderMessage> book = new TreeSet<>(OrderBook.SORT_SELL);
		OrderMessage o1 = createOrder(OrderType.SELL_LIMIT, 123, 99, 9);
		OrderMessage o2 = createOrder(OrderType.SELL_LIMIT, 123, 99, 6);
		assertTrue(o1.equals(o2));
		assertEquals(o1.hashCode(), o2.hashCode());
		assertTrue(book.add(o1));
		assertTrue(book.remove(o2));
	}

	@Test
	public void testOrderInTreeSet2() {
		Set<OrderMessage> book = new TreeSet<>(OrderBook.SORT_SELL);
		Random random = new Random();
		for (long price = 100; price < 1000; price++) {
			OrderMessage o = createOrder(OrderType.SELL_LIMIT, random.nextInt(1000000), price, random.nextInt(1000000));
			assertTrue(book.add(o));
			o.amount = BigDecimal.ZERO;
			assertTrue(book.remove(o));
		}
	}

	@Test
	public void testSellComparator() {
		long[][] SELLS = new long[][] { //
				{ 12, 99, 13, 100 }, //
				{ 12, 98, 13, 100 }, //
				{ 12, 100, 13, 100 }, //
				{ 88, 100, 99, 100 }, //
		};
		for (long[] params : SELLS) {
			assertEquals(-1, OrderBook.SORT_SELL.compare(createOrder(OrderType.SELL_LIMIT, params[0], params[1], 1),
					createOrder(OrderType.SELL_LIMIT, params[2], params[3], 1)));
			assertEquals(1, OrderBook.SORT_SELL.compare(createOrder(OrderType.SELL_LIMIT, params[2], params[3], 1),
					createOrder(OrderType.SELL_LIMIT, params[0], params[1], 1)));
		}
	}

	static OrderMessage createOrder(OrderType type, long id, long price, long amount) {
		Order o = new Order();
		o.type = type;
		o.id = id;
		o.price = BigDecimal.valueOf(price);
		o.amount = BigDecimal.valueOf(amount);
		return new OrderMessage(id, o);
	}

}
