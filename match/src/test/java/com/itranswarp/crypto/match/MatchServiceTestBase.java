package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.sequence.OrderDirection;
import com.itranswarp.crypto.sequence.message.CancelOrderMessage;
import com.itranswarp.crypto.sequence.message.LimitOrderMessage;
import com.itranswarp.crypto.sequence.message.MarketOrderMessage;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.crypto.symbol.Symbol;

public class MatchServiceTestBase {

	protected MessageQueue<OrderMessage> orderMessageQueue;
	protected MessageQueue<MatchMessage> matchMessageQueue;
	protected MessageQueue<TickMessage> tickMessageQueue;

	protected MatchService matchService;

	@After
	public void shutdownMatchService() throws Exception {
		this.matchService.shutdown();
	}

	@Before
	public void setUpMatchService() throws Exception {
		this.orderMessageQueue = new MessageQueue<>(1000);
		this.matchMessageQueue = new MessageQueue<>(1000);
		this.tickMessageQueue = new MessageQueue<>(1000);
		this.matchService = new MatchService();
		this.matchService.orderMessageQueue = this.orderMessageQueue;
		this.matchService.matchMessageQueue = this.matchMessageQueue;
		this.matchService.tickMessageQueue = this.tickMessageQueue;
		this.matchService.start();
		sendOrders(
				// seq type price amount
				"001 sell_limit 2703.33 4.4444", //
				"006 sell_limit 2702.22 3.3333", //
				"002 sell_limit 2702.22 2.2222", //
				"004 sell_limit 2701.11 1.1111", //
				// -----------------------------------
				"003 buy_limit  2699.99 0.1111", //
				"005 buy_limit  2688.88 2.2222", //
				"007 buy_limit  2688.88 2.2222", //
				"009 buy_limit  2666.66 4.4444" //
		);
	}

	protected void assertOrderBook(String... lines) throws InterruptedException {
		Thread.sleep(100);
		String[] sells = this.matchService.sellBook.dumps(true);
		String[] buys = this.matchService.buyBook.dumps(false);
		String[] all = new String[sells.length + buys.length];
		System.arraycopy(sells, 0, all, 0, sells.length);
		System.arraycopy(buys, 0, all, sells.length, buys.length);
		assertEquals(lines.length, all.length);
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String item = all[i];
			assertOrderItemEquals(line, item);
		}
	}

	void assertOrderItemEquals(String expectedItem, String actualItem) {
		Matcher expectedMatcher = orderBookItemPattern.matcher(expectedItem);
		Matcher actualMatcher = orderBookItemPattern.matcher(actualItem);
		assertTrue(expectedMatcher.matches());
		assertTrue(actualMatcher.matches());

		String msg = "Check %s failed: expected=" + expectedItem + ", actual=" + actualItem;

		long expectedSeq = Long.parseLong(expectedMatcher.group(1));
		long actualSeq = Long.parseLong(actualMatcher.group(1));
		assertEquals(String.format(msg, "seqId"), expectedSeq, actualSeq);

		String expectedType = expectedMatcher.group(2);
		String actualType = actualMatcher.group(2);
		assertEquals(String.format(msg, "orderType"), expectedType, actualType);

		BigDecimal expectedPrice = new BigDecimal(expectedMatcher.group(3));
		BigDecimal actualPrice = new BigDecimal(actualMatcher.group(3));
		assertEquals(String.format(msg, "price"), expectedPrice, actualPrice);

		BigDecimal expectedAmount = new BigDecimal(expectedMatcher.group(4));
		BigDecimal actualAmount = new BigDecimal(actualMatcher.group(4));
		assertEquals(String.format(msg, "amount"), expectedAmount, actualAmount);
	}

	protected void assertTicks(String... lines) throws InterruptedException {
		if (lines.length == 0) {
			assertNull(this.tickMessageQueue.getMessage(1000));
			return;
		}
		for (String line : lines) {
			TickMessage tick = this.tickMessageQueue.getMessage(1000);
			BigDecimal price = new BigDecimal(line.split(" ")[0]);
			BigDecimal amount = new BigDecimal(line.split(" ")[1]);
			assertEquals(price, tick.price);
			assertEquals(amount, tick.amount);
		}
	}

	protected void sendOrders(String... lines) throws InterruptedException {
		Arrays.sort(lines);
		for (String line : lines) {
			Matcher matcher = limitOrderPattern.matcher(line);
			if (matcher.matches()) {
				long seqId = Long.parseLong(matcher.group(1));
				String buyOrSell = matcher.group(2);
				BigDecimal price = new BigDecimal(matcher.group(3));
				BigDecimal amount = new BigDecimal(matcher.group(4));
				OrderMessage msg = newLimitOrderMessage(seqId, buyOrSell, price, amount);
				this.orderMessageQueue.sendMessage(msg);
				continue;
			}
			matcher = marketOrderPattern.matcher(line);
			if (matcher.matches()) {
				long seqId = Long.parseLong(matcher.group(1));
				String buyOrSell = matcher.group(2);
				BigDecimal amount = new BigDecimal(matcher.group(3));
				OrderMessage msg = newMarketOrderMessage(seqId, buyOrSell, amount);
				this.orderMessageQueue.sendMessage(msg);
				continue;
			}
			matcher = cancelOrderPattern.matcher(line);
			if (matcher.matches()) {
				long seqId = Long.parseLong(matcher.group(1));
				String buyOrSell = matcher.group(2);
				long refSeqId = Long.parseLong(matcher.group(3));
				BigDecimal price = new BigDecimal(matcher.group(4));
				OrderMessage msg = newCancelOrderMessage(seqId, buyOrSell, refSeqId, price);
				this.orderMessageQueue.sendMessage(msg);
				continue;
			}
			throw new RuntimeException("Load order book failed: " + line);
		}
	}

	OrderMessage newLimitOrderMessage(long seqId, String buyOrSell, BigDecimal price, BigDecimal amount) {
		Order order = new Order();
		order.seqId = order.id = seqId;
		order.userId = 10001;
		order.createdAt = System.currentTimeMillis();
		order.type = buyOrSell.equals("buy") ? OrderType.BUY_LIMIT : OrderType.SELL_LIMIT;
		order.price = price;
		order.amount = amount;
		return new LimitOrderMessage(Symbol.BTC_USD,
				order.type == OrderType.BUY_LIMIT ? OrderDirection.BUY : OrderDirection.SELL, order.userId, order.seqId,
				order.id, order.createdAt, order.price, order.amount);
	}

	OrderMessage newMarketOrderMessage(long seqId, String buyOrSell, BigDecimal amount) {
		Order order = new Order();
		order.seqId = order.id = seqId;
		order.userId = 10001;
		order.createdAt = System.currentTimeMillis();
		order.type = buyOrSell.equals("buy") ? OrderType.BUY_MARKET : OrderType.SELL_MARKET;
		order.price = null;
		order.amount = amount;
		return new MarketOrderMessage(Symbol.BTC_USD,
				order.type == OrderType.BUY_MARKET ? OrderDirection.BUY : OrderDirection.SELL, order.userId,
				order.seqId, order.id, order.createdAt, order.amount);
	}

	OrderMessage newCancelOrderMessage(long seqId, String buyOrSell, long refSeqId, BigDecimal price) {
		Order order = new Order();
		order.id = seqId;
		order.createdAt = System.currentTimeMillis();
		order.type = buyOrSell.equals("buy") ? OrderType.CANCEL_BUY_LIMIT : OrderType.CANCEL_SELL_LIMIT;
		order.refSeqId = order.refOrderId = refSeqId;
		order.price = price;
		order.amount = null;
		return new CancelOrderMessage(Symbol.BTC_USD,
				order.type == OrderType.CANCEL_BUY_LIMIT ? OrderDirection.BUY : OrderDirection.SELL, order.userId,
				order.seqId, order.id, order.createdAt, order.refSeqId, order.refOrderId, order.price);
	}

	final Pattern cancelOrderPattern = Pattern.compile("^(\\d+)\\s+cancel_(buy|sell)_limit\\s+(\\d+)\\s+([\\d\\.]+)$");

	final Pattern limitOrderPattern = Pattern.compile("^(\\d+)\\s+(buy|sell)_limit\\s+([\\d\\.]+)\\s+([\\d\\.]+)$");

	final Pattern marketOrderPattern = Pattern.compile("^(\\d+)\\s+(buy|sell)_market\\s+([\\d\\.]+)$");

	final Pattern orderBookItemPattern = Pattern.compile("^(\\d+)\\s+(buy|sell)\\s+([\\d\\.]+)\\s+([\\d\\.]+)$");
}
