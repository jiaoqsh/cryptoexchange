package com.itranswarp.crypto.service.model;

import com.itranswarp.crypto.match.TickMessage;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.sequence.message.OrderMessage;

/**
 * @file: OrderEvent
 * @author: jiaoqsh
 * @since: 2018/02/02
 */
public class OrderEvent {

    private Order order;

    private OrderMessage orderMessage;

    private MatchMessage matchMessage;

    private TickMessage tickMessage;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderMessage getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(OrderMessage orderMessage) {
        this.orderMessage = orderMessage;
    }

    public MatchMessage getMatchMessage() {
        return matchMessage;
    }

    public void setMatchMessage(MatchMessage matchMessage) {
        this.matchMessage = matchMessage;
    }

    public TickMessage getTickMessage() {
        return tickMessage;
    }

    public void setTickMessage(TickMessage tickMessage) {
        this.tickMessage = tickMessage;
    }
}
