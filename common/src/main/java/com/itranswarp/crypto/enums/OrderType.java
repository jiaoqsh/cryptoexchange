package com.itranswarp.crypto.enums;

public enum OrderType {

	/**
	 * Buy with limit price.
	 */
	BUY_LIMIT(1),

	/**
	 * Sell with limit price.
	 */
	SELL_LIMIT(2),

	/**
	 * Buy with market price.
	 */
	BUY_MARKET(3),

	/**
	 * Sell with market price.
	 */
	SELL_MARKET(4),

	/**
	 * Cancel buy order.
	 */
	CANCEL_BUY_LIMIT(5),

	/**
	 * Cancel sell order.
	 */
	CANCEL_SELL_LIMIT(6);

	public final int value;

	private OrderType(int value) {
		this.value = value;
	}
}
