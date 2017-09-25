package com.itranswarp.crypto.match;

import java.math.BigDecimal;

/**
 * Snapshot for order book.
 * 
 * @author liaoxuefeng
 */
public class OrderSnapshot {

	public final BigDecimal price;
	public BigDecimal amount;

	public OrderSnapshot(BigDecimal price, BigDecimal amount) {
		this.price = price;
		this.amount = amount;
	}

}
