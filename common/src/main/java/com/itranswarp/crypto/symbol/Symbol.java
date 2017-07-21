package com.itranswarp.crypto.symbol;

import java.util.Objects;

/**
 * Define symbol by base currency / quote currency. e.g. "BTC/CNY"
 * 
 * @author liaoxuefeng
 */
public class Symbol {

	public static final Symbol BTC_CNY = new Symbol(Currency.BTC, Currency.CNY);

	/**
	 * Base currency.
	 */
	public final Currency base;

	/**
	 * Quote currency.
	 */
	public final Currency quote;

	Symbol(Currency base, Currency quote) {
		this.base = base;
		this.quote = quote;
	}

	public Symbol valueOf(String symbol) {
		int n = symbol.indexOf('/');
		if (n == (-1)) {
			throw new IllegalArgumentException("Invalid symbol: " + symbol);
		}
		Currency base = Currency.valueOf(symbol.substring(0, n));
		Currency quote = Currency.valueOf(symbol.substring(n + 1));
		return new Symbol(base, quote);
	}

	/**
	 * Name of symbol.
	 * 
	 * @return Name like "BTC/USD"
	 */
	public String name() {
		return String.format("%s/%s", this.base.name(), this.quote.name());
	}

	/**
	 * Same as name().
	 */
	@Override
	public String toString() {
		return name();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Symbol) {
			Symbol sym = (Symbol) o;
			return this.base == sym.base && this.quote == sym.quote;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.base.name(), this.quote.name());
	}
}
