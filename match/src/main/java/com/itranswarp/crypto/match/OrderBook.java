package com.itranswarp.crypto.match;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.itranswarp.crypto.sequence.OrderMessage;

/**
 * Order book for sell or buy.
 * 
 * @author liaoxuefeng
 */
public class OrderBook {

	public static enum OrderBookType {
		SELL, BUY
	}

	/**
	 * Sorted by sell.
	 */
	static final Comparator<OrderMessage> SORT_SELL = new Comparator<OrderMessage>() {
		@Override
		public int compare(OrderMessage o1, OrderMessage o2) {
			BigDecimal p1 = o1.price;
			BigDecimal p2 = o2.price;
			int n = p1.compareTo(p2);
			if (n < 0) {
				return -1;
			}
			if (n > 0) {
				return 1;
			}
			long i1 = o1.id;
			long i2 = o2.id;
			if (i1 < i2) {
				return -1;
			}
			if (i1 > i2) {
				return 1;
			}
			return 0;
		}
	};

	/**
	 * Sorted by buy.
	 */
	static final Comparator<OrderMessage> SORT_BUY = new Comparator<OrderMessage>() {
		@Override
		public int compare(OrderMessage o1, OrderMessage o2) {
			BigDecimal p1 = o1.price;
			BigDecimal p2 = o2.price;
			int n = p1.compareTo(p2);
			if (n < 0) {
				return 1;
			}
			if (n > 0) {
				return -1;
			}
			long i1 = o1.id;
			long i2 = o2.id;
			if (i1 < i2) {
				return -1;
			}
			if (i1 > i2) {
				return 1;
			}
			return 0;
		}
	};

	final OrderBookType type;
	// holds all orders:
	final TreeSet<OrderMessage> book;

	public OrderBook(OrderBookType type) {
		this.type = type;
		this.book = new TreeSet<>(type == OrderBookType.SELL ? SORT_SELL : SORT_BUY);
	}

	public List<OrderSnapshot> getSnapshot(int maxItems) {
		List<OrderSnapshot> list = new ArrayList<>(maxItems);
		OrderSnapshot lastLiveOrder = null;
		Iterator<OrderMessage> it = this.book.iterator();
		while (it.hasNext()) {
			OrderMessage order = it.next();
			if (lastLiveOrder == null) {
				// init:
				lastLiveOrder = new OrderSnapshot(order.price, order.amount);
				list.add(lastLiveOrder);
			} else {
				if (order.price.equals(lastLiveOrder.price)) {
					lastLiveOrder.amount = lastLiveOrder.amount.add(order.amount);
				} else {
					if (list.size() >= maxItems) {
						break;
					}
					lastLiveOrder = new OrderSnapshot(order.price, order.amount);
					list.add(lastLiveOrder);
				}
			}
		}
		return list;
	}

	/**
	 * Get first order from order book, or null if order book is empty.
	 * 
	 * @return Order or null if empty.
	 */
	public OrderMessage getFirst() {
		return this.book.isEmpty() ? null : this.book.first();
	}

	public boolean remove(OrderMessage order) {
		return this.book.remove(order);
	}

	public boolean add(OrderMessage order) {
		return this.book.add(order);
	}

	public int size() {
		return this.book.size();
	}

	public void dump(boolean reverse) {
		int n = 0;
		Iterator<OrderMessage> it = reverse ? this.book.descendingIterator() : this.book.iterator();
		while (it.hasNext() && n < 10) {
			n++;
			System.out.println(it.next());
		}
	}

}
