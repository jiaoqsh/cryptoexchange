package com.itranswarp.crypto.order;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.symbol.Symbol;

@Component
public class OrderService extends AbstractService {

	@Autowired
	AccountService accountService;

	@Autowired
	OrderHandler orderHandler;

	@Autowired
	@Qualifier("orderSequenceQueue")
	MessageQueue<Order> orderSequenceQueue;

	public Order createBuyLimitOrder(long userId, Symbol symbol, BigDecimal price, BigDecimal amount)
			throws InterruptedException {
		Order order = orderHandler.createBuyLimitOrder(userId, symbol, price, amount);
		logger.info("order created: " + order);
		orderSequenceQueue.sendMessage(order);
		return order;
	}

	public Order createSellLimitOrder(long userId, Symbol symbol, BigDecimal price, BigDecimal amount)
			throws InterruptedException {
		Order order = orderHandler.createSellLimitOrder(userId, symbol, price, amount);
		logger.info("order created: " + order);
		orderSequenceQueue.sendMessage(order);
		return order;
	}
}
