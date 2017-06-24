package com.itranswarp.crypto.match;

/**
 * Snapshot for order book.
 * 
 * @author liaoxuefeng
 */
public class SnapshotOrder {

	public final long price;
	public long amount;

	public SnapshotOrder(long price, long amount) {
		this.price = price;
		this.amount = amount;
	}

}
