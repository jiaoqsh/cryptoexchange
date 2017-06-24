package com.itranswarp.crypto.symbol;

import static org.junit.Assert.*;

import org.junit.Test;

public class CurrencyTest {

	@Test
	public void testDisplay() {
		assertEquals("123456.79", Currency.USD.display(1234567890));
		assertEquals("12345.68", Currency.USD.display(123456789));
		assertEquals("1234.57", Currency.USD.display(12345678));
		assertEquals("123.46", Currency.USD.display(1234567));
		assertEquals("12.35", Currency.USD.display(123456));
		assertEquals("1.23", Currency.USD.display(12345));
		assertEquals("1.20", Currency.USD.display(12000));
		assertEquals("0.12", Currency.USD.display(1200));
		assertEquals("-123.46", Currency.USD.display(-1234567));
		assertEquals("-12.35", Currency.USD.display(-123456));
		assertEquals("-1.23", Currency.USD.display(-12345));
		assertEquals("-1.20", Currency.USD.display(-12000));
		assertEquals("-0.12", Currency.USD.display(-1200));
	}

}
