package com.itranswarp.crypto.quotation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.itranswarp.crypto.match.TickMessage;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.symbol.Symbol;

/**
 * Process tick and order book snapshot to produce market quotation.
 * 
 * @author liaoxuefeng
 */
public class QuotationService {

	final ZoneId zoneId = ZoneId.systemDefault();
	final MessageQueue<TickMessage> quotationMessageQueue;

	public QuotationService(MessageQueue<TickMessage> quotationQueue) {
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
