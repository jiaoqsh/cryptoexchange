package com.itranswarp.crypto.match;

import java.math.BigDecimal;

public class MatchRecord {

	public final long takerOrderId;
	public final long makerOrderId;
	public final BigDecimal matchPrice;
	public final BigDecimal matchAmount;

	public MatchRecord(long takerOrderId, long makerOrderId, BigDecimal matchPrice, BigDecimal matchAmount) {
		this.takerOrderId = takerOrderId;
		this.makerOrderId = makerOrderId;
		this.matchPrice = matchPrice;
		this.matchAmount = matchAmount;
	}

}
