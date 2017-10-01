package com.itranswarp.crypto.match;

import java.math.BigDecimal;
import java.util.Objects;

import com.itranswarp.crypto.sequence.OrderDirection;

public class OrderItem {
	public final OrderDirection direction;
	public final long seqId;
	public final long orderId;

	public final BigDecimal price;
	public BigDecimal amount;

	public final long createdAt;

	public OrderItem(OrderDirection direction, long seqId, long orderId, BigDecimal price, BigDecimal amount,
			long createdAt) {
		this.direction = direction;
		this.seqId = seqId;
		this.orderId = orderId;

		this.price = price;
		this.amount = amount;

		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OrderItem) {
			OrderItem item = (OrderItem) o;
			return Objects.equals(this.seqId, item.seqId) && Objects.equals(this.price, item.price);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.seqId, this.price);
	}

	@Override
	public String toString() {
		return String.format("OrderItem: %s $%.2f %.4f seqId=%d, orderId=%d", this.direction, this.price, this.amount,
				this.seqId, this.orderId);
	}
}
