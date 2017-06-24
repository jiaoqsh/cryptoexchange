package com.itranswarp.crypto.user;

import java.util.Optional;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;

/**
 * Holds user context in thread-local.
 * 
 * @author liaoxuefeng
 */
public class UserContext implements AutoCloseable {

	static final ThreadLocal<User> threadLocalUser = new ThreadLocal<>();

	/**
	 * Get current user, or throw exception if user not signin.
	 * 
	 * @return User object.
	 */
	public static User getRequiredCurrentUser() {
		Optional<User> user = getCurrentUser();
		return user.orElseThrow(() -> {
			return new ApiException(ApiError.AUTH_SIGNIN_REQUIRED);
		});
	}

	/**
	 * Get current user.
	 * 
	 * @return Optional user object.
	 */
	public static Optional<User> getCurrentUser() {
		return Optional.ofNullable(threadLocalUser.get());
	}

	public UserContext(User user) {
		threadLocalUser.set(user);
	}

	@Override
	public void close() {
		threadLocalUser.remove();
	}

}
