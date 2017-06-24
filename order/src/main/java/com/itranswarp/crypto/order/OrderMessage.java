package com.itranswarp.crypto.order;

public class OrderMessage {

	public final long id;
	public final long userId;
	public final OrderType type;
	public final long price;
	public long amount;
	public long createdAt;

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
		return String.format("%s: $%4d %4d   id:%d", (type == OrderType.BUY_LIMIT ? "B" : "S"), price, amount, id);
	}
}
