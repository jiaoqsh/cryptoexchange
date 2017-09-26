package com.itranswarp.crypto.match;

import java.math.BigDecimal;

import com.itranswarp.crypto.enums.OrderStatus;

public class MatchRecord {

	public final long takerOrderId;
	public final long makerOrderId;
	public final BigDecimal matchPrice;
	public final BigDecimal matchAmount;
	public final OrderStatus makerStatus;

	public MatchRecord(long takerOrderId, long makerOrderId, BigDecimal matchPrice, BigDecimal matchAmount,
			OrderStatus makerStatus) {
		this.takerOrderId = takerOrderId;
		this.makerOrderId = makerOrderId;
		this.matchPrice = matchPrice;
		this.matchAmount = matchAmount;
		this.makerStatus = makerStatus;
	}

}
