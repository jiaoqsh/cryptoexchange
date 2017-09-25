package com.itranswarp.crypto.order;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.symbol.Symbol;

@Component
@Transactional
public class OrderHandler extends AbstractService {

	@Autowired
	AccountService accountService;

	public Order createBuyLimitOrder(long userId, Symbol symbol, BigDecimal price, BigDecimal amount)
			throws InterruptedException {
		amount = symbol.base.adjust(amount);
		price = symbol.quote.adjust(price);
		// frozen:
		accountService.freeze(userId, symbol.quote, price.multiply(amount));
		// create order:
		Order order = new Order();
		order.userId = userId;
		order.symbol = symbol;
		order.amount = amount;
		order.price = price;
		order.filledAmount = BigDecimal.ZERO;
		order.type = OrderType.BUY_LIMIT;
		order.status = OrderStatus.SUBMITTED;
		db.save(order);
		return order;
	}

	public Order createSellLimitOrder(long userId, Symbol symbol, BigDecimal price, BigDecimal amount)
			throws InterruptedException {
		amount = symbol.base.adjust(amount);
		price = symbol.quote.adjust(price);
		// frozen:
		accountService.freeze(userId, symbol.base, amount);
		// create order:
		Order order = new Order();
		order.userId = userId;
		order.symbol = symbol;
		order.amount = amount;
		order.price = price;
		order.filledAmount = BigDecimal.ZERO;
		order.type = OrderType.SELL_LIMIT;
		order.status = OrderStatus.SUBMITTED;
		db.save(order);
		return order;
	}
}
