package com.itranswarp.crypto.order;

import java.math.BigDecimal;

public class OrderMessage {

	public final long id;
	public final long userId;
	public final OrderType type;
	public final BigDecimal price;
	public BigDecimal amount;
	public final long createdAt;

	/**
	 * Create order message from Order entity.
	 */
	public OrderMessage(Order order) {
		this.id = order.id;
		this.userId = order.userId;
		this.type = order.type;
		this.price = order.price;
		this.amount = order.amount;
		this.createdAt = order.createdAt;
	}

	@Override
	public boolean equals(Object o) {
		return this.id == ((OrderMessage) o).id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.id);
	}

	public String toString() {
		return String.format("%s: $%.2f %.4f   id:%d", (type == OrderType.BUY_LIMIT ? "B" : "S"), price, amount, id);
	}
}
