package com.itranswarp.crypto.order;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.enums.OrderType;
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

	public Order createCancelOrder(OrderType cancelType, Order orderToBeCancelled) throws InterruptedException {
		Order order = orderHandler.createCancelOrder(cancelType, orderToBeCancelled);
		logger.info("order cancelled: id=" + order.id + ", cancel=" + orderToBeCancelled);
		orderSequenceQueue.sendMessage(order);
		return order;
	}

	public Order getOrder(long id) {
		Order order = db.fetch(Order.class, id);
		if (order == null) {
			throw new ApiException(ApiError.ORDER_NOT_FOUND);
		}
		return order;
	}

}
