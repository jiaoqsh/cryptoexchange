package com.itranswarp.crypto.match.message;

import java.math.BigDecimal;

public class CancelledMessage implements MatchMessage {

	public final boolean cancelled;
	public final long cancelId;
	public final long cancelledOrderId;
	public final BigDecimal cancelledOrderAmount;
	public final long timestamp;

	public CancelledMessage(boolean cancelled, long cancelId, long cancelledOrderId, BigDecimal cancelledOrderAmount,
			long timestamp) {
		this.cancelled = cancelled;
		this.cancelId = cancelId;
		this.cancelledOrderId = cancelledOrderId;
		this.cancelledOrderAmount = cancelledOrderAmount;
		this.timestamp = timestamp;
	}

}
