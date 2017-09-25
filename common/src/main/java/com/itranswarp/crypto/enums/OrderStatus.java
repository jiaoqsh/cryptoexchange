package com.itranswarp.crypto.enums;

public enum OrderStatus {

	/**
	 * Order is submitted and pending into sequence queue.
	 */
	SUBMITTED(1),

	/**
	 * Order is sequenced.
	 */
	SEQUENCED(2),

	/**
	 * Order is fully filled.
	 */
	FULLY_FILLED(3),

	/**
	 * Order is partially filled and still in order book.
	 */
	PARTIAL_FILLED(4),

	/**
	 * Order is partially cancelled.
	 */
	PARTIAL_CANCELLED(5),

	/**
	 * Order is fully cancelled.
	 */
	FULLY_CANCELLED(6);

	public final int value;

	private OrderStatus(int value) {
		this.value = value;
	}
}
