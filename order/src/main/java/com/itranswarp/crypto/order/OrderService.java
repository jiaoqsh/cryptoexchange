package com.itranswarp.crypto.order;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.symbol.Symbol;

@Component
public class OrderService extends AbstractService {

	@Autowired
	OrderHandlerService orderHandlerService;

	@Autowired
	@Qualifier("orderMessageQueue")
	MessageQueue<OrderMessage> orderMessageQueue;

	public Order createBuyLimitOrder(Symbol symbol, BigDecimal price, BigDecimal amount) throws InterruptedException {
		Order order = new Order();
		order.baseCurrency = symbol.base;
		order.quoteCurrency = symbol.quote;
		order.amount = symbol.base.adjust(amount);
		order.price = symbol.quote.adjust(price);
		order.type = OrderType.BUY_LIMIT;
		orderHandlerService.saveOrder(order);
		orderMessageQueue.sendMessage(new OrderMessage(order));
		return order;
	}

	public Order createSellLimitOrder(Symbol symbol, BigDecimal price, BigDecimal amount) throws InterruptedException {
		Order order = new Order();
		order.baseCurrency = symbol.base;
		order.quoteCurrency = symbol.quote;
		order.amount = symbol.base.adjust(amount);
		order.price = symbol.quote.adjust(price);
		order.type = OrderType.SELL_LIMIT;
		orderHandlerService.saveOrder(order);
		orderMessageQueue.sendMessage(new OrderMessage(order));
		return order;
	}
}
