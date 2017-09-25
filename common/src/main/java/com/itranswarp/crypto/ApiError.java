package com.itranswarp.crypto;

public enum ApiError {

	PARAMETER_INVALID,

	AUTH_SIGNIN_REQUIRED, AUTH_SIGNIN_FAILED, AUTH_CANNOT_SIGNIN, AUTH_CANNOT_CHANGE_PWD, AUTH_BAD_OLD_PWD, AUTH_USER_LOCKED,

	ACCOUNT_FREEZE_FAILED, ACCOUNT_UNFREEZE_FAILED, ACCOUNT_ADD_BALANCE_FAILED, USER_EMAIL_EXIST,

	/**
	 * Invalid order.
	 */
	ORDER_INVALID,

	/**
	 * All other internal server error.
	 */
	INTERNAL_SERVER_ERROR;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
