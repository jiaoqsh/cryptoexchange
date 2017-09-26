package com.itranswarp.crypto.sequence;

import java.math.BigDecimal;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.symbol.Symbol;

public class OrderMessage {

	public final long orderId;
	public final long seqId;
	public final long userId;
	public final Symbol symbol;
	public final OrderType type;
	public final BigDecimal price;
	public BigDecimal amount;
	public final long createdAt;

	/**
	 * Create order message from Order entity.
	 */
	public OrderMessage(long seqId, Order order) {
		this.seqId = seqId;
		this.orderId = order.id;
		this.userId = order.userId;
		this.symbol = order.symbol;
		this.type = order.type;
		this.price = order.price;
		this.amount = order.amount;
		this.createdAt = order.createdAt;
	}

	@Override
	public boolean equals(Object o) {
		return this.seqId == ((OrderMessage) o).seqId;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.seqId);
	}

	public String toString() {
		return String.format("%s order: $%.2f %.4f orderId=%d, seqId=%d",
				(this.type == OrderType.BUY_LIMIT ? "B" : "S"), this.price, this.amount, this.orderId, this.seqId);
	}
}
