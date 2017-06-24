package com.itranswarp.crypto.account;

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

import com.itranswarp.crypto.store.DbTestConfiguration;
import com.itranswarp.crypto.store.DbTestBase;
import com.itranswarp.crypto.symbol.Currency;

@Configuration
@ComponentScan("com.itranswarp.crypto.account")
@ComponentScan("com.itranswarp.crypto.store")
class AccountServiceConfiguration {

}

@Commit
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:/test.properties")
@ContextConfiguration(classes = { DbTestConfiguration.class, AccountServiceConfiguration.class })
public class AccountServiceTest extends DbTestBase {

	@Autowired
	AccountService accountService;

	@Before
	public void setUp() {
		createTable(SpotAccount.class);
		createTable(FrozenAccount.class);
	}

	@Test
	public void testDeposit() {
		final long USER_A = 12345;
		SpotAccount sa = accountService.getSpotAccount(USER_A, Currency.CNY);
		assertNotNull(sa);
		assertTrue(sa.id > 0);
		assertEquals(0, sa.balance);
		accountService.deposit(USER_A, Currency.CNY, 130000);
		SpotAccount sa2 = accountService.getSpotAccount(USER_A, Currency.CNY);
		assertEquals(130000, sa2.balance);
	}

	@Test
	public void testFreeze() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.CNY, 130000);
		accountService.freeze(USER_A, Currency.CNY, 120000);
	}

	@Test
	public void testUnfreeze() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.CNY, 130000);
		accountService.freeze(USER_A, Currency.CNY, 120000);
		accountService.unfreeze(USER_A, Currency.CNY, 120000);
	}

	@Test
	public void testGetSpotAccount() {
		final long USER_A = 12345;
		SpotAccount sa = accountService.getSpotAccount(USER_A, Currency.CNY);
		assertNotNull(sa);
		assertEquals(0, sa.balance);
	}

	@Test
	public void testGetFrozenAccount() {
		final long USER_A = 12345;
		FrozenAccount fa = accountService.getFrozenAccount(USER_A, Currency.CNY);
		assertNotNull(fa);
		assertEquals(0, fa.balance);
	}

}
