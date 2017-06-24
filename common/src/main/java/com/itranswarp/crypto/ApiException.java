package com.itranswarp.crypto;

/**
 * Base exception for API.
 * 
 * @author liaoxuefeng
 */
public class ApiException extends RuntimeException {

	public final String error;
	public final String data;

	public ApiException(ApiError error) {
		super(error.toString());
		this.error = error.toString();
		this.data = null;
	}

	public ApiException(ApiError error, String data) {
		super(error.toString());
		this.error = error.toString();
		this.data = data;
	}

	public ApiException(ApiError error, String data, String message) {
		super(message);
		this.error = error.toString();
		this.data = data;
	}

	public ApiErrorResponse toErrorResponse() {
		return new ApiErrorResponse(this.error, this.data, this.getMessage());
	}

	public static class ApiErrorResponse {
		public final String error;
		public final String data;
		public final String message;

		public ApiErrorResponse(String error, String data, String message) {
			this.error = error;
			this.data = data;
			this.message = message;
		}
	}
}
