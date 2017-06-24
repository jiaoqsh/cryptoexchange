package com.itranswarp.crypto.symbol;

/**
 * Define symbol by base currency / quote currency.
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
}
