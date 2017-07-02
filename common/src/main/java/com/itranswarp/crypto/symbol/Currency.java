package com.itranswarp.crypto.symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Define currency constants.
 * 
 * @author liaoxuefeng
 */
public enum Currency {

	USD(2), CNY(2), JPY(2), BTC(4), LTC(4), ETH(4);

	private final int scale;

	private Currency(int scale) {
		this.scale = scale;
	}

	/**
	 * Scale for computing.
	 * 
	 * @return Scale of computing.
	 */
	public int getScale() {
		return this.scale;
	}

	/**
	 * Set the scale if necessary and rounding with down.
	 * 
	 * @param value
	 *            Input value of BigDecimal.
	 * @return Adjusted value.
	 */
	public BigDecimal adjust(BigDecimal value) {
		if (value.scale() > this.scale) {
			value = value.setScale(this.scale, RoundingMode.DOWN);
		}
		return value;
	}

	public String display(BigDecimal value) {
		return String.format("%." + this.scale + "f", adjust(value));
	}
}
