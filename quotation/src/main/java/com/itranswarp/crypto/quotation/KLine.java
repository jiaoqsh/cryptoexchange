package com.itranswarp.crypto.quotation;

public class KLine {

	public static enum Type {
		K_1MIN, K_5MIN, K_15MIN, K_30MIN, K_60MIN, K_1DAY, K_1WEEK, K_1MONTH
	}

	// include:
	public final long startTime;

	// exclude:
	public final long endTime;

	public final long openPrice;
	public final long closePrice;
	public final long highPrice;
	public final long lowPrice;

	public final long amount;

	public KLine(long startTime, long endTime, long openPrice, long closePrice, long highPrice, long lowPrice,
			long amount) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.openPrice = openPrice;
		this.closePrice = closePrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.amount = amount;
	}
}
