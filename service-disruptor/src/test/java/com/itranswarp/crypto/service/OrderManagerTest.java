package com.itranswarp.crypto.service;

import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.symbol.Symbol;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;

/**
 * Created by jiaoqsh on 18/2/2.
 */
public class OrderManagerTest extends AbstractTests {

    @Autowired
    private OrderManager orderManager;

    @Test
    public void createBuyLimitOrder() throws Exception {
        Order order = orderManager.createBuyLimitOrder(10001, Symbol.BTC_USD, new BigDecimal(2700.01), new BigDecimal(0.9999));
        assertNotNull(order);
    }

    @Test
    public void createSellLimitOrder() throws Exception {
        Order order = orderManager.createSellLimitOrder(10001, Symbol.BTC_USD, new BigDecimal(2700.01), new BigDecimal(0.6666));
        assertNotNull(order);
    }

}