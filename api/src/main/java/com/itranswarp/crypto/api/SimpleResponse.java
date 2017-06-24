package com.itranswarp.crypto.api;

public class SimpleResponse<T> {

	public final T data;

	public static <K> SimpleResponse<K> of(K data) {
		return new SimpleResponse<>(data);
	}

	private SimpleResponse(T data) {
		this.data = data;
	}

}
