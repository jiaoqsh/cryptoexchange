package com.itranswarp.crypto.quotation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.crypto.RunnableResource;
import com.itranswarp.crypto.match.TickMessage;
import com.itranswarp.crypto.queue.MessageQueue;

public class TickHandler implements RunnableResource {

	static final int BATCH_SIZE = 10;

	final Logger logger = LoggerFactory.getLogger(getClass());

	final MessageQueue<TickMessage> tickQueue;
	Thread processThread = null;

	public TickHandler(MessageQueue<TickMessage> tickerQueue) {
		this.tickQueue = tickerQueue;
	}

	public BigDecimal amount = new BigDecimal(0);
	public final List<String> list = new ArrayList<>();

	@Override
	public synchronized void start() {
		if (processThread != null) {
			throw new IllegalStateException("Cannot re-start ticker handler.");
		}
		processThread = new Thread() {
			public void run() {
				try {
					while (true) {
						TickMessage tick = tickQueue.getMessage();
						logger.info("Receive tick: " + tick);
						store(tick, tick.time / 1000 == 0);
						ZonedDateTime dt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tick.time), DEFAULT_TIMEZONE);
						list.add(dt.toLocalTime() + " $ " + tick.price + ", " + amount);
						amount = amount.add(tick.amount);
					}
				} catch (InterruptedException e) {
					logger.warn("TickerHandler was interrupted.");
				}
				// store unsaved ticker:
				storeNow();
			}
		};
		processThread.start();
	}

	@Override
	public synchronized void shutdown() {
		if (processThread == null) {
			throw new IllegalStateException("Ticker handler is not running.");
		}
		logger.info("shutdown TickerHandler...");
		processThread.interrupt();
		try {
			processThread.join();
		} catch (InterruptedException e) {
			logger.warn("TickerHandler thread failed in join().", e);
		}
		processThread = null;
	}

	private List<TickMessage> cachedTickers = new ArrayList<>(BATCH_SIZE);

	private void store(TickMessage ticker, boolean forceFlush) {
		if (ticker != null) {
			cachedTickers.add(ticker);
		}
		if (forceFlush) {
			storeNow();
		}
	}

	private void storeNow() {
		// TODO: store all:
		if (cachedTickers.isEmpty()) {
			return;
		}
		cachedTickers.clear();
	}

	@Override
	public String toString() {
		return String.join("\n", list) + "\nAmount: " + this.amount;
	}

	public List<KLine> toSeconds() {
		return null;
	}

	static final ZoneId DEFAULT_TIMEZONE = ZoneId.systemDefault();

}
