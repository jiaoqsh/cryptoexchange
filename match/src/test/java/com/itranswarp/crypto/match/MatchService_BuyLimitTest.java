package com.itranswarp.crypto.match;

import org.junit.Test;

public class MatchService_BuyLimitTest extends MatchServiceTestBase {

	@Test
	public void testBuyLimit_LowerPrice_NotFilled() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2700.01 0.9999");
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
				"100 buy  2700.01 0.9999", //
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_Buy_1_NotFilled() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2699.99 0.9999");
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
				"100 buy  2699.99 0.9999", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_MoreLowerPrice_Buy_1_NotFilled() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2697.77 0.9999");
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
				"100 buy  2697.77 0.9999", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_LowestPrice_Buy_5_NotFilled() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2655.55 0.9999");
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
				"009 buy  2666.66 4.4444", //
				"100 buy  2655.55 0.9999" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_FullyFilled_Sell_1() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2701.11 1.0001");
		// check ticks:
		assertTicks("2701.11 1.0001");
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 0.1110", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_UpperPrice_FullyFilled_Sell_1() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2701.12 1.0001");
		// check ticks:
		assertTicks("2701.11 1.0001");
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 2.2222", //
				"004 sell 2701.11 0.1110", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_FullyFilled_Sell_2() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2702.22 3.1122");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.0011" //
		);
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 0.2211", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_UpperPrice_FullyFilled_Sell_2() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2702.23 3.1122");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.0011" //
		);
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 3.3333", //
				"002 sell 2702.22 0.2211", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_FullyFilled_Sell_3() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2702.22 6.3344");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.0011" //
		);
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 0.3322", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_UpperPrice_FullyFilled_Sell_3() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2702.23 6.3344");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.0011" //
		);
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 4.4444", //
				"006 sell 2702.22 0.3322", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_FullyFilled_Sell_4() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2703.33 7.7777");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.3333", //
				"2703.33 1.1111" //
		);
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 3.3333", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_UpperPrice_FullyFilled_Sell_4() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2703.34 7.7777");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.3333", //
				"2703.33 1.1111" //
		);
		// check order books:
		assertOrderBook(
				// seq type price amount
				"001 sell 2703.33 3.3333", //
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_Just_FullyFilled_All_Sells() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2703.33 11.1110");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.3333", //
				"2703.33 4.4444");
		// check order books:
		assertOrderBook(
				// seq type price amount
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_UpperPrice_Just_FullyFilled_All_Sells() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2703.34 11.1110");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.3333", //
				"2703.33 4.4444");
		// check order books:
		assertOrderBook(
				// seq type price amount
				// -----------------------------------
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_SamePrice_FullyFilled_All_Sells_And_Left() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2703.33 12.1111");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.3333", //
				"2703.33 4.4444");
		// check order books:
		assertOrderBook(
				// seq type price amount
				// -----------------------------------
				"100 buy  2703.33 1.0001", //
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}

	@Test
	public void testBuyLimit_UpperPrice_FullyFilled_All_Sells_And_Left() throws Exception {
		// buy:
		sendOrders("100 buy_limit 2703.34 12.1111");
		// check ticks:
		assertTicks( //
				"2701.11 1.1111", //
				"2702.22 2.2222", //
				"2702.22 3.3333", //
				"2703.33 4.4444");
		// check order books:
		assertOrderBook(
				// seq type price amount
				// -----------------------------------
				"100 buy  2703.34 1.0001", //
				"003 buy  2699.99 0.1111", //
				"005 buy  2688.88 2.2222", //
				"007 buy  2688.88 2.2222", //
				"009 buy  2666.66 4.4444" //
		);
	}
}
