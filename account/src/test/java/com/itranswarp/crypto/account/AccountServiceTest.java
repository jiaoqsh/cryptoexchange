package com.itranswarp.crypto.account;

import static org.junit.Assert.*;

import java.math.BigDecimal;

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
import com.itranswarp.crypto.ApiException;
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
	}

	@Test
	public void testDeposit() {
		final long USER_A = 12345;
		SpotAccount sa = accountService.getSpotAccount(USER_A, Currency.USD);
		assertNotNull(sa);
		assertTrue(sa.id > 0);
		assertTrue(BigDecimal.ZERO.compareTo(sa.balance) == 0);
		accountService.deposit(USER_A, Currency.USD, new BigDecimal("130000.12"));
		// query:
		SpotAccount sa2 = accountService.getSpotAccount(USER_A, Currency.USD);
		assertTrue(new BigDecimal("130000.12").compareTo(sa2.balance) == 0);
	}

	@Test
	public void testFreeze() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.USD, new BigDecimal("130000.25"));
		accountService.freeze(USER_A, Currency.USD, new BigDecimal("120000.12"));
		// query:
		SpotAccount sa2 = accountService.getSpotAccount(USER_A, Currency.USD);
		assertTrue(new BigDecimal("10000.13").compareTo(sa2.balance) == 0);
	}

	@Test
	public void testFreezeAll() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.USD, new BigDecimal("130000.25"));
		accountService.freeze(USER_A, Currency.USD, new BigDecimal("130000.25"));
		// query:
		SpotAccount sa2 = accountService.getSpotAccount(USER_A, Currency.USD);
		assertTrue(BigDecimal.ZERO.compareTo(sa2.balance) == 0);
	}

	@Test(expected = ApiException.class)
	public void testFreezeFailed() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.USD, new BigDecimal("130000.25"));
		accountService.freeze(USER_A, Currency.USD, new BigDecimal("130000.26"));
	}

	@Test
	public void testUnfreeze() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.USD, new BigDecimal("130000.25"));
		accountService.freeze(USER_A, Currency.USD, new BigDecimal("120000.12"));
		accountService.unfreeze(USER_A, Currency.USD, new BigDecimal("120000.12"));
		// query:
		SpotAccount sa2 = accountService.getSpotAccount(USER_A, Currency.USD);
		assertTrue(new BigDecimal("130000.25").compareTo(sa2.balance) == 0);
	}

	@Test(expected = ApiException.class)
	public void testUnfreezeFailed() {
		final long USER_A = 12345;
		accountService.deposit(USER_A, Currency.USD, new BigDecimal("130000.25"));
		accountService.freeze(USER_A, Currency.USD, new BigDecimal("120000.12"));
		accountService.unfreeze(USER_A, Currency.USD, new BigDecimal("120000.13"));
	}

	@Test
	public void testGetSpotAccount() {
		final long USER_A = 12345;
		SpotAccount sa = accountService.getSpotAccount(USER_A, Currency.USD);
		assertNotNull(sa);
		assertTrue(BigDecimal.ZERO.compareTo(sa.balance) == 0);
	}

}
