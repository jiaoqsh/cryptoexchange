package com.itranswarp.crypto.sequence.message;

import java.math.BigDecimal;

import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.symbol.Symbol;

public class CancelOrderMessage extends OrderMessage {

	public final long refSeqId;
	public final long refOrderId;
	public final BigDecimal price;

	public CancelOrderMessage(Symbol symbol, OrderDirection direction, long userId, long seqId, long orderId,
			long createdAt, long refSeqId, long refOrderId, BigDecimal price) {
		super(symbol, direction, userId, seqId, orderId, createdAt);
		this.refSeqId = refSeqId;
		this.refOrderId = refOrderId;
		this.price = price;
	}

	@Override
	public String toString() {
		return String.format("%s, refSeqId=%s, refOrderId=%s, price=%.2f", super.toString(), this.refSeqId,
				this.refOrderId, this.price);
	}
}
