package com.itranswarp.crypto.match;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.itranswarp.crypto.order.OrderMessage;

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
			if (n != 0) {
				return n;
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
			if (n != 0) {
				return n;
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

	// holds all orders:
	TreeSet<OrderMessage> book;

	public OrderBook(OrderBookType type) {
		this.book = new TreeSet<>(type == OrderBookType.SELL ? SORT_SELL : SORT_BUY);
	}

	public List<SnapshotOrder> getSnapshot(int maxItems, BigDecimal priceDelta) {
		List<SnapshotOrder> list = new ArrayList<>(maxItems);
		SnapshotOrder lastLiveOrder = null;
		BigDecimal currentLevelPrice = null;
		BigDecimal nextLevelPrice = null;
		Iterator<OrderMessage> it = this.book.iterator();
		while (it.hasNext() && list.size() < maxItems) {
			OrderMessage order = it.next();
			if (currentLevelPrice == null) {
				// init:
				currentLevelPrice = order.price;
				nextLevelPrice = currentLevelPrice.add(priceDelta);
				lastLiveOrder = new SnapshotOrder(order.price, order.amount);
				list.add(lastLiveOrder);
			} else {
				// if current level = 123.45,
				// so next level = 123.45 + delta = 124.45 (suppose delta = 1)
				if (nextLevelPrice.compareTo(order.price) > 0) {
					// add to current level:
					lastLiveOrder.amount = lastLiveOrder.amount.add(order.amount);
				} else {
					// new depth level:
					while (nextLevelPrice.compareTo(order.price) < 0) {
						currentLevelPrice = nextLevelPrice;
						nextLevelPrice = currentLevelPrice.add(priceDelta);
					}
					lastLiveOrder = new SnapshotOrder(order.price, order.amount);
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
