package com.itranswarp.crypto.quotation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.itranswarp.crypto.match.Tick;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.symbol.Symbol;

public class QuotationService {

	final ZoneId zoneId = ZoneId.systemDefault();
	final MessageQueue<Tick> quotationMessageQueue;

	public QuotationService(MessageQueue<Tick> quotationQueue) {
		this.quotationMessageQueue = quotationQueue;
	}

	/**
	 * Query KLine data.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<KLine> query(Symbol symbol, KLine.Type type, LocalDateTime start, LocalDateTime end) {
		//
		return null;
	}

	public void init() {
		// load recent ticks from db:
	}

}
