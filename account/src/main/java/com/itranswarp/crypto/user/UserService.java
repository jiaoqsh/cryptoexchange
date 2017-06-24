package com.itranswarp.crypto.user;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.util.HashUtil;

@Component
@Transactional
public class UserService extends AbstractService {

	public User signin(String email, String passwd) {
		checkEmail(email);
		checkPassword(passwd);
		User user = getByEmail(email);
		// find PasswordAuth by user id:
		PasswordAuth pa = db.from(PasswordAuth.class).where("userId = ?", user.id).first();
		if (pa == null) {
			throw new ApiException(ApiError.AUTH_CANNOT_SIGNIN);
		}
		// check password hash:
		String hash = sha1Password(user.id, passwd);
		if (!hash.equals(pa.passwd)) {
			throw new ApiException(ApiError.AUTH_SIGNIN_FAILED);
		}
		return user;
	}

	public User createUser(String email, String passwd, String name) {
		checkEmail(email);
		checkPassword(passwd);
		checkName(name);
		User user = new User();
		user.email = email;
		user.type = User.UserType.NORMAL;
		user.name = name;
		db.save(user);
		PasswordAuth pa = new PasswordAuth();
		pa.userId = user.id;
		pa.passwd = sha1Password(user.id, passwd);
		db.save(pa);
		return user;
	}

	public User getByEmail(String email) {
		User user = db.from(User.class).where("email = ?", email).first();
		if (user == null) {
			throw new ApiException(ApiError.AUTH_SIGNIN_FAILED);
		}
		return user;
	}

	public void changePassword(String email, String oldPasswd, String newPasswd) {
		checkEmail(email);
		checkPassword(oldPasswd);
		checkPassword(newPasswd);
		User user = getByEmail(email);
		// find PasswordAuth by user id:
		PasswordAuth pa = db.from(PasswordAuth.class).where("userId = ?", user.id).first();
		if (pa == null) {
			throw new ApiException(ApiError.AUTH_CANNOT_CHANGE_PWD);
		}
		// check password hash:
		String hash = sha1Password(user.id, oldPasswd);
		if (!hash.equals(pa.passwd)) {
			throw new ApiException(ApiError.AUTH_BAD_OLD_PWD);
		}
		// change to new password:
		pa.passwd = sha1Password(user.id, newPasswd);
		db.update(pa);
	}

	String sha1Password(long userId, String originPasswd) {
		String payload = userId + ":" + originPasswd;
		return HashUtil.sha1(payload.getBytes(StandardCharsets.UTF_8));
	}

	void checkEmail(String email) {
		if (email == null || !PATTERN_EMAIL.matcher(email).matches()) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "email");
		}
	}

	void checkPassword(String passwd) {
		if (passwd == null || !PATTERN_PASSWD.matcher(passwd).matches()) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "passwd");
		}
	}

	void checkName(String name) {
		if (name == null || name.isEmpty() || name.length() > 50) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "name");
		}
	}

	// email pattern:
	static final Pattern PATTERN_EMAIL = Pattern
			.compile("^[_a-z0-9-\\+]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9]+)*(\\.[a-z]{2,})$");

	// SHA-1 password:
	static final Pattern PATTERN_PASSWD = Pattern.compile("[a-f0-9]{40}");

}
