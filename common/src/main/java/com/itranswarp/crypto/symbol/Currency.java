package com.itranswarp.crypto.symbol;

/**
 * Define currency constants.
 * 
 * @author liaoxuefeng
 */
public enum Currency {

	USD(4, 2), CNY(4, 2), JPY(4, 2), BTC(6, 4), LTC(6, 4), ETH(6, 4);

	private final int computeScale;
	private final int displayScale;

	private Currency(int computeScale, int displayScale) {
		this.computeScale = computeScale;
		this.displayScale = displayScale;
	}

	/**
	 * Scale for computing.
	 * 
	 * @return Scale of computing.
	 */
	public int getComputeScale() {
		return this.computeScale;
	}

	/**
	 * Scale for display.
	 * 
	 * @return Scale of display.
	 */
	public int getDisplayScale() {
		return this.displayScale;
	}

	public String display(long value) {
		double d = value / Math.pow(10, this.computeScale);
		return String.format("%." + this.displayScale + "f", d);
	}
}
