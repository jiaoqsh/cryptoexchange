package com.itranswarp.crypto.enums;

public enum MatchResultType {

	/**
	 * Some orders were matched.
	 */
	MATCHED,

	/**
	 * A maker's order was cancelled.
	 */
	CANCELLED,

	/**
	 * A cancelled order failed to cancel the maker's order.
	 */
	CANCEL_FAILED;

}
