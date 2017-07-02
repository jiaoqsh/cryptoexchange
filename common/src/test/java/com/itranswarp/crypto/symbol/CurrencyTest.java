package com.itranswarp.crypto.symbol;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class CurrencyTest {

	@Test
	public void testAdjust() {
		assertTrue(new BigDecimal("12.34").compareTo(Currency.USD.adjust(new BigDecimal("12.34567"))) == 0);
		assertTrue(new BigDecimal("12.34").compareTo(Currency.USD.adjust(new BigDecimal("12.345"))) == 0);
		assertTrue(new BigDecimal("12.34").compareTo(Currency.USD.adjust(new BigDecimal("12.344"))) == 0);
		assertTrue(new BigDecimal("12.34").compareTo(Currency.USD.adjust(new BigDecimal("12.340"))) == 0);
		assertTrue(new BigDecimal("12.3").compareTo(Currency.USD.adjust(new BigDecimal("12.30"))) == 0);
		assertTrue(new BigDecimal("12").compareTo(Currency.USD.adjust(new BigDecimal("12.00"))) == 0);
		// negative:
		assertTrue(new BigDecimal("-12.34").compareTo(Currency.USD.adjust(new BigDecimal("-12.34567"))) == 0);
		assertTrue(new BigDecimal("-12.34").compareTo(Currency.USD.adjust(new BigDecimal("-12.345"))) == 0);
		assertTrue(new BigDecimal("-12.34").compareTo(Currency.USD.adjust(new BigDecimal("-12.344"))) == 0);
		assertTrue(new BigDecimal("-12.34").compareTo(Currency.USD.adjust(new BigDecimal("-12.340"))) == 0);
		assertTrue(new BigDecimal("-12.3").compareTo(Currency.USD.adjust(new BigDecimal("-12.30"))) == 0);
		assertTrue(new BigDecimal("-12").compareTo(Currency.USD.adjust(new BigDecimal("-12.00"))) == 0);
	}

	@Test
	public void testDisplay() {
		assertEquals("123456.78", Currency.USD.display(new BigDecimal("123456.789")));
		assertEquals("123456.78", Currency.USD.display(new BigDecimal("123456.781")));
		assertEquals("123456.78", Currency.USD.display(new BigDecimal("123456.780")));
		assertEquals("123456.78", Currency.USD.display(new BigDecimal("123456.78")));
		assertEquals("123456.70", Currency.USD.display(new BigDecimal("123456.7")));
		assertEquals("-1234.56", Currency.USD.display(new BigDecimal("-1234.5678")));
		assertEquals("-1234.56", Currency.USD.display(new BigDecimal("-1234.567")));
		assertEquals("-1234.56", Currency.USD.display(new BigDecimal("-1234.560")));
		assertEquals("-1234.56", Currency.USD.display(new BigDecimal("-1234.561")));
		assertEquals("-1234.56", Currency.USD.display(new BigDecimal("-1234.56")));
		assertEquals("-1234.50", Currency.USD.display(new BigDecimal("-1234.5")));
	}

}
