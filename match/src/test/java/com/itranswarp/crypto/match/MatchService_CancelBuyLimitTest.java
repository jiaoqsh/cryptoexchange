package com.itranswarp.crypto.match;

import org.junit.Test;

public class MatchService_CancelBuyLimitTest extends MatchServiceTestBase {

	@Test
	public void testCancel_BuyLimit_1_OK() throws Exception {
		// cancel:
		sendOrders("100 cancel_buy_limit 003 2699.99");
		// check ticks:
		assertTicks();
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 1.1111", //
				// -----------------------------------
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testCancel_BuyLimit_1_Failed_for_PriceNotMatch() throws Exception {
		// cancel:
		sendOrders("100 cancel_buy_limit 003 2699.88");
		// check ticks:
		assertTicks();
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 1.1111", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testCancel_BuyLimit_2_OK() throws Exception {
		// cancel:
		sendOrders("100 cancel_buy_limit 005 2688.88");
		// check ticks:
		assertTicks();
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 1.1111", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testCancel_BuyLimit_3_OK() throws Exception {
		// cancel:
		sendOrders("100 cancel_buy_limit 007 2688.88");
		// check ticks:
		assertTicks();
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 1.1111", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

}
