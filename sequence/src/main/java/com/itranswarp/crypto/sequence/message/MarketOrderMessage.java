package com.itranswarp.crypto.sequence.message;

import java.math.BigDecimal;

import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.symbol.Symbol;

public class MarketOrderMessage extends OrderMessage {

	public BigDecimal amount;

	public MarketOrderMessage(Symbol symbol, OrderDirection direction, long userId, long seqId, long orderId,
			long createdAt, BigDecimal amount) {
		super(symbol, direction, userId, seqId, orderId, createdAt);
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("%s, amount=%.4f", super.toString(), this.amount);
	}
}
