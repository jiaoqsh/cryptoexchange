package com.itranswarp.crypto.clearing;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.match.MatchRecord;
import com.itranswarp.crypto.match.MatchResult;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.store.AbstractService;

@Component
@Transactional
public class ClearingHandlerService extends AbstractService {

	public void processMatchResult(MatchResult matchResult) {
		for (MatchRecord record : matchResult.getMatchRecords()) {
			Order taker = db.get(Order.class, record.takerOrderId);
			Order maker = db.get(Order.class, record.makerOrderId);
		}
	}

}
