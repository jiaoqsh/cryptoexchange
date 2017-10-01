package com.itranswarp.crypto.sequence.message;

import java.math.BigDecimal;

import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.symbol.Symbol;

public class LimitOrderMessage extends OrderMessage {

	public final BigDecimal price;
	public final BigDecimal amount;

	public LimitOrderMessage(Symbol symbol, OrderDirection direction, long userId, long seqId, long orderId,
			long createdAt, BigDecimal price, BigDecimal amount) {
		super(symbol, direction, userId, seqId, orderId, createdAt);
		this.price = price;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("%s, price=%.2f, amount=%.4f", super.toString(), this.price, this.amount);
	}
}
