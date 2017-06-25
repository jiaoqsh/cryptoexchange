package com.itranswarp.crypto.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractService;

@Component
public class OrderService extends AbstractService {

	final MessageQueue<OrderMessage> orderMessageQueue;

	public OrderService(@Autowired @Qualifier("orderMessageQueue") MessageQueue<OrderMessage> orderMessageQueue) {
		this.orderMessageQueue = orderMessageQueue;
	}

	public void createBuyLimitOrder(long price, long amount) throws InterruptedException {
		Order order = new Order();
		order.amount = amount;
		order.price = price;
		order.type = OrderType.BUY_LIMIT;
		db.save(order);
		orderMessageQueue.sendMessage(new OrderMessage(order));
	}

	public void createSellLimitOrder(long price, long amount) throws InterruptedException {
		Order order = new Order();
		order.amount = amount;
		order.price = price;
		order.type = OrderType.SELL_LIMIT;
		db.save(order);
		orderMessageQueue.sendMessage(new OrderMessage(order));
	}
}
