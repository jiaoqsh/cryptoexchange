package com.itranswarp.crypto.sequence.message;

import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.symbol.Symbol;

public abstract class OrderMessage {

	public final Symbol symbol;
	public final OrderDirection direction;

	public final long userId;

	public final long seqId;
	public final long orderId;

	public final long createdAt;

	public OrderMessage(Symbol symbol, OrderDirection direction, long userId, long seqId, long orderId,
			long createdAt) {
		this.symbol = symbol;
		this.direction = direction;
		this.userId = userId;
		this.seqId = seqId;
		this.orderId = orderId;
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o) {
		return this.seqId == ((MarketOrderMessage) o).seqId;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.seqId);
	}

	@Override
	public String toString() {
		return String.format("%s: %s seqId=%d, orderId=%d", this.getClass().getSimpleName(), this.direction.name(),
				this.seqId, this.orderId);
	}
}
