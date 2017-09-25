package com.itranswarp.crypto.sequence;

import java.math.BigDecimal;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.symbol.Symbol;

public class OrderMessage {

	public final long id;
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
		this.id = order.id;
		this.userId = order.userId;
		this.symbol = order.symbol;
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
