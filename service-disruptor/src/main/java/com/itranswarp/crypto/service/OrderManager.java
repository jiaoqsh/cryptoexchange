package com.itranswarp.crypto.service;

import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.order.OrderHandler;
import com.itranswarp.crypto.service.producer.OrderEventProducer;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.symbol.Symbol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @file: OrderManager
 * @author: jiaoqsh
 * @since: 2018/02/02
 */
@Component
public class OrderManager extends AbstractService {

    @Autowired
    private OrderHandler orderHandler;

    @Autowired
    private OrderEventProducer orderEventProducer;

    public Order createBuyLimitOrder(long userId, Symbol symbol, BigDecimal price, BigDecimal amount) throws InterruptedException {
        Order order = orderHandler.createBuyLimitOrder(userId, symbol, price, amount);
        logger.info("order created|order={}", order);
        orderEventProducer.onData(order);
        return order;
    }

    public Order createSellLimitOrder(long userId, Symbol symbol, BigDecimal price, BigDecimal amount) throws InterruptedException {
        Order order = orderHandler.createSellLimitOrder(userId, symbol, price, amount);
        logger.info("order created|order={}", order);
        orderEventProducer.onData(order);
        return order;
    }

}
