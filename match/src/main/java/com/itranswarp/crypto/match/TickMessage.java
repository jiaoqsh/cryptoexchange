package com.itranswarp.crypto.match;

import java.math.BigDecimal;

import com.itranswarp.crypto.symbol.Symbol;

public class TickMessage {

	public final Symbol symbol;

	public final long time;

	public final BigDecimal price;

	public final BigDecimal amount;

	public TickMessage(Symbol symbol, long time, BigDecimal price, BigDecimal amount) {
		this.symbol = symbol;
		this.time = time;
		this.price = price;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("TickMessage(symbol=%s, time=%d, price=%.2f, amount=%.4f)", symbol.name(), time, price,
				amount);
	}
}
