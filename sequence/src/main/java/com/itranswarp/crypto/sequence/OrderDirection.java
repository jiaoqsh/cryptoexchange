package com.itranswarp.crypto.sequence;

public enum OrderDirection {

	BUY(1), SELL(2);

	private OrderDirection(int value) {
		this.value = value;
	}

	public final int value;
}
