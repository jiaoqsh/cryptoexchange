package com.itranswarp.crypto.match;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.itranswarp.crypto.sequence.OrderDirection;

/**
 * Order book for sell or buy.
 * 
 * @author liaoxuefeng
 */
public class OrderBook {

	/**
	 * Sorted by sell.
	 */
	static final Comparator<OrderItem> SORT_SELL = new Comparator<OrderItem>() {
		@Override
		public int compare(OrderItem o1, OrderItem o2) {
			BigDecimal p1 = o1.price;
			BigDecimal p2 = o2.price;
			int n = p1.compareTo(p2);
			if (n < 0) {
				return -1;
			}
			if (n > 0) {
				return 1;
			}
			long i1 = o1.seqId;
			long i2 = o2.seqId;
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
	static final Comparator<OrderItem> SORT_BUY = new Comparator<OrderItem>() {
		@Override
		public int compare(OrderItem o1, OrderItem o2) {
			BigDecimal p1 = o1.price;
			BigDecimal p2 = o2.price;
			int n = p1.compareTo(p2);
			if (n < 0) {
				return 1;
			}
			if (n > 0) {
				return -1;
			}
			long i1 = o1.seqId;
			long i2 = o2.seqId;
			if (i1 < i2) {
				return -1;
			}
			if (i1 > i2) {
				return 1;
			}
			return 0;
		}
	};

	// BUY or SELL?
	final OrderDirection direction;

	// holds all orders:
	final TreeMap<OrderItem, OrderItem> book;

	public OrderBook(OrderDirection direction) {
		this.direction = direction;
		this.book = new TreeMap<>(direction == OrderDirection.BUY ? SORT_BUY : SORT_SELL);
	}

	public List<OrderSnapshot> getSnapshot(int maxItems) {
		List<OrderSnapshot> list = new ArrayList<>(maxItems);
		OrderSnapshot lastLiveOrder = null;
		for (Entry<OrderItem, OrderItem> entry : book.entrySet()) {
			OrderItem order = entry.getKey();
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
	public OrderItem getFirst() {
		return this.book.isEmpty() ? null : this.book.firstKey();
	}

	/**
	 * Remove the order from order book.
	 * 
	 * @param order
	 *            The order message.
	 * @return True if exists and are removed.
	 */
	public OrderItem remove(OrderItem order) {
		return this.book.remove(order);
	}

	public boolean add(OrderItem order) {
		return this.book.put(order, order) == null;
	}

	public int size() {
		return this.book.size();
	}

	public void dump(boolean reverse) {
		int n = 0;
		for (Entry<OrderItem, OrderItem> entry : (reverse ? this.book.descendingMap() : this.book).entrySet()) {
			n++;
			System.out.println(entry.getKey());
			if (n >= 10) {
				break;
			}
		}
	}

	/**
	 * Only for unit test.
	 * 
	 * @param reverse
	 * @return
	 */
	String[] dumps(boolean reverse) {
		List<String> list = new ArrayList<>();
		for (Entry<OrderItem, OrderItem> entry : (reverse ? this.book.descendingMap() : this.book).entrySet()) {
			OrderItem item = entry.getKey();
			String line = item.seqId + " " + item.direction.name().toLowerCase() + " " + item.price + " " + item.amount;
			list.add(line);
		}
		return list.toArray(new String[list.size()]);
	}

}
