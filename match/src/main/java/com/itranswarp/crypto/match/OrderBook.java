package com.itranswarp.crypto.match;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.order.OrderMessage;
import com.itranswarp.crypto.order.OrderType;

/**
 * Order book for sell or buy.
 * 
 * @author liaoxuefeng
 */
public class OrderBook {

	/**
	 * Sorted by sell.
	 */
	public static final Comparator<OrderMessage> SORT_SELL = new Comparator<OrderMessage>() {
		@Override
		public int compare(OrderMessage o1, OrderMessage o2) {
			long p1 = o1.price;
			long p2 = o2.price;
			if (p1 < p2) {
				return -1;
			}
			if (p1 > p2) {
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
	public static final Comparator<OrderMessage> SORT_BUY = new Comparator<OrderMessage>() {
		@Override
		public int compare(OrderMessage o1, OrderMessage o2) {
			long p1 = o1.price;
			long p2 = o2.price;
			if (p1 < p2) {
				return 1;
			}
			if (p1 > p2) {
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

	// holds all orders:
	TreeSet<OrderMessage> book;

	public OrderBook(Comparator<OrderMessage> comparator) {
		this.book = new TreeSet<>(comparator);
	}

	public List<SnapshotOrder> getSnapshot(int maxItems, long priceDelta) {
		List<SnapshotOrder> list = new ArrayList<>(maxItems);
		SnapshotOrder lastLiveOrder = null;
		long currentLevelPrice = 0;
		long nextLevelPrice = 0;
		Iterator<OrderMessage> it = this.book.iterator();
		while (it.hasNext() && list.size() < maxItems) {
			OrderMessage order = it.next();
			if (currentLevelPrice == 0) {
				// init:
				currentLevelPrice = order.price;
				nextLevelPrice = currentLevelPrice + priceDelta;
				lastLiveOrder = new SnapshotOrder(order.price, order.amount);
				list.add(lastLiveOrder);
			} else {
				if (order.price < nextLevelPrice) {
					// add to current level:
					lastLiveOrder.amount += order.amount;
				} else {
					// new depth level:
					while (nextLevelPrice < order.price) {
						currentLevelPrice = nextLevelPrice;
						nextLevelPrice = currentLevelPrice + priceDelta;
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

	public static void main(String[] args) {
		// sell:
		OrderBook sell = new OrderBook(OrderBook.SORT_SELL);
		for (long id = 1; id < 20; id++) {
			OrderMessage o = createOrder(OrderType.SELL_LIMIT, id, randomLong(110, 130), randomLong(1, 100));
			sell.add(o);
		}
		sell.dump(true);
		System.out.println("------------------------------");
		// buy:
		OrderBook buy = new OrderBook(OrderBook.SORT_BUY);
		for (long id = 100; id < 120; id++) {
			OrderMessage o = createOrder(OrderType.BUY_LIMIT, id, randomLong(100, 120), randomLong(1, 100));
			buy.add(o);
		}
		buy.dump(false);
	}

	static OrderMessage createOrder(OrderType type, long id, long price, long amount) {
		Order o = new Order();
		o.type = type;
		o.id = id;
		o.price = price;
		o.amount = amount;
		return new OrderMessage(o);
	}

	static final Random random = new Random();

	static long randomLong(int min, int max) {
		return random.nextInt(max - min) + min;
	}

}
