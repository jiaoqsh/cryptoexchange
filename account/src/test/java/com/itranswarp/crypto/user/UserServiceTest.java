package com.itranswarp.crypto.user;

import static org.junit.Assert.*;

import org.junit.Test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.store.DbTestConfiguration;
import com.itranswarp.crypto.store.DbTestBase;

@Configuration
@ComponentScan("com.itranswarp.crypto.user")
@ComponentScan("com.itranswarp.crypto.store")
class UserServiceConfiguration {

}

@Commit
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:/test.properties")
@ContextConfiguration(classes = { DbTestConfiguration.class, UserServiceConfiguration.class })
public class UserServiceTest extends DbTestBase {

	@Autowired
	UserService userService;

	@Before
	public void setUp() {
		createTable(PasswordAuth.class);
		createTable(User.class);
	}

	@Test
	public void testCheckEmail() {
		userService.checkEmail("test@example.com");
		userService.checkEmail("test@example.com.cn");
		userService.checkEmail("test@example.online");
		userService.checkEmail("test@vip.example.online");
		userService.checkEmail("test-test@example.com");
		userService.checkEmail("test12@example.com.cn");
		userService.checkEmail("test_123@example.online");
		userService.checkEmail("t@vip.example.online");
	}

	@Test
	public void testCreateUser() {
		final String email = "test@example.com";
		final String passwd = "0123456789012345678901234567890123456789";
		User user = userService.createUser(email, passwd, "Example");
		assertNotNull(user);
		assertEquals(email, user.email);
		assertEquals("Example", user.name);
		assertEquals(System.currentTimeMillis(), user.createdAt, 1000.0);
		assertEquals(System.currentTimeMillis(), user.updatedAt, 1000.0);
		// check signin:
		User u = userService.signin(email, passwd);
		assertNotNull(u);
		assertEquals(email, u.email);
		assertEquals("Example", u.name);
	}

	@Test(expected = ApiException.class)
	public void testSigninFailed() {
		final String email = "test@example.com";
		final String passwd = "0123456789012345678901234567890123456789";
		User user = userService.createUser(email, passwd, "Example");
		assertNotNull(user);
		// check signin:
		userService.signin(email, "f123456789012345678901234567890123456789");
	}

	@Test
	public void testChangePassword() {
		final String email = "test@example.com";
		final String passwd = "0123456789012345678901234567890123456789";
		final String newPasswd = "ffffffffff012345678901234567890123456789";
		User user = userService.createUser(email, passwd, "Example");
		assertNotNull(user);
		// check signin:
		userService.signin(email, passwd);
		userService.changePassword(email, passwd, newPasswd);
		// check signin with new passwd:
		User u = userService.signin(email, newPasswd);
		assertNotNull(u);
		assertEquals(email, u.email);
	}

}
