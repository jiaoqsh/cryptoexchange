package com.itranswarp.crypto.match;

import java.math.BigDecimal;

public class Tick {

	public final long time;

	public final BigDecimal price;

	public final BigDecimal amount;

	public Tick(long time, BigDecimal price, BigDecimal amount) {
		this.time = time;
		this.price = price;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("Tick(time=%d, price=%.2f, amount=%.4f)", time, price, amount);
	}
}
