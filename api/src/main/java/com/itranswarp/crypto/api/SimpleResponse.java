package com.itranswarp.crypto.api;

import java.util.Collections;
import java.util.Map;

public class SimpleResponse {

	public static <T> Map<String, T> of(T value) {
		return Collections.singletonMap("data", value);
	}

	public static <T> Map<String, T> of(String key, T value) {
		return Collections.singletonMap(key, value);
	}

}
