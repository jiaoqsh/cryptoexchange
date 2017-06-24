package com.itranswarp.crypto.match;

public class Tick {

	public final long time;

	public final long price;

	public final long amount;

	public Tick(long time, long price, long amount) {
		this.time = time;
		this.price = price;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("Tick(time=%d, price=%d, amount=%d)", time, price, amount);
	}
}
