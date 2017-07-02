package com.itranswarp.crypto.order;

public enum OrderStatus {

	/**
	 * Order is submitted, but not match yet.
	 */
	SUBMITTED(1),

	/**
	 * Order is fully filled.
	 */
	FULLY_FILLED(2),

	/**
	 * Order is partially filled and still in order book.
	 */
	PARTIAL_FILLED(3),

	/**
	 * Order is partially cancelled.
	 */
	PARTIAL_CANCELLED(4),

	/**
	 * Order is fully cancelled.
	 */
	FULLY_CANCELLED(5);

	public final int value;

	private OrderStatus(int value) {
		this.value = value;
	}
}
