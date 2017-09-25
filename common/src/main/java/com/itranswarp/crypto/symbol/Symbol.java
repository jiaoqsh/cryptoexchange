package com.itranswarp.crypto.symbol;

/**
 * Define symbol by base currency / quote currency. e.g. "BTC_USD"
 * 
 * @author liaoxuefeng
 */
public enum Symbol {

	BTC_USD(Currency.BTC, Currency.USD);

	/**
	 * Base currency.
	 */
	public final Currency base;

	/**
	 * Quote currency.
	 */
	public final Currency quote;

	private Symbol(Currency base, Currency quote) {
		this.base = base;
		this.quote = quote;
	}

}
