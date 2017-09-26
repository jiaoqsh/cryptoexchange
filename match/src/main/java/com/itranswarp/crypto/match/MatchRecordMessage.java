package com.itranswarp.crypto.match;

import java.math.BigDecimal;

import com.itranswarp.crypto.enums.OrderStatus;

public class MatchRecordMessage {

	public final long takerOrderId;
	public final long makerOrderId;
	public final BigDecimal matchPrice;
	public final BigDecimal matchAmount;
	public final OrderStatus makerStatus;

	public MatchRecordMessage(long takerOrderId, long makerOrderId, BigDecimal matchPrice, BigDecimal matchAmount,
			OrderStatus makerStatus) {
		this.takerOrderId = takerOrderId;
		this.makerOrderId = makerOrderId;
		this.matchPrice = matchPrice;
		this.matchAmount = matchAmount;
		this.makerStatus = makerStatus;
	}

	@Override
	public String toString() {
		return String.format("Taker: %d, maker: %d, price=%7.2f, amount=%7.2f, maker=%s", this.takerOrderId,
				this.makerOrderId, this.matchPrice, this.matchAmount, this.makerStatus);
	}
}
