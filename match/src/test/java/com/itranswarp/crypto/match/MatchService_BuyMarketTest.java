package com.itranswarp.crypto.match;

import org.junit.Test;

public class MatchService_BuyMarketTest extends MatchServiceTestBase {

	@Test
	public void testBuyMarket_Partial_Eat_Sell_1() throws Exception {
		// buy:
		sendOrders("100 buy_market 3000.01");
		// check ticks:
		assertTicks("2701.11 1.1106");
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 0.0005", //
				// --------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

}
