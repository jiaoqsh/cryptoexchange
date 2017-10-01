package com.itranswarp.crypto.clearing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.enums.MatchResultType;
import com.itranswarp.crypto.enums.MatchType;
import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.match.MatchRecordMessage;
import com.itranswarp.crypto.match.message.CancelledMessage;
import com.itranswarp.crypto.match.message.MatchResultMessage;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.store.AbstractService;

@Component
@Transactional
public class ClearingHandlerService extends AbstractService {

	@Autowired
	AccountService accountService;

	/**
	 * Process a MatchResult. A MatchResult contains one or more MatchRecords,
	 * which reference to the same taker.
	 * 
	 * @param matchResult
	 *            MatchResult object.
	 */
	public void processMatched(MatchResultMessage matchResult) {
		final long timestamp = matchResult.timestamp;
		// insert will failed if already processed:
		MatchResult mr = new MatchResult();
		mr.type = MatchResultType.MATCHED;
		mr.orderId = matchResult.orderId;
		db.save(mr);
		// a match result contains one taker and one or more makers:
		Order takerOrder = null;
		List<MatchRecord> recordCollector = new ArrayList<>();
		BigDecimal totalSpent = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (MatchRecordMessage record : matchResult.getMatchRecords()) {
			BigDecimal price = record.matchPrice;
			BigDecimal amount = record.matchAmount;
			if (takerOrder == null) {
				takerOrder = db.get(Order.class, record.takerOrderId);
			}
			Order makerOrder = db.get(Order.class, record.makerOrderId);
			clearMakerOrder(takerOrder, makerOrder, price, amount, record.makerStatus, timestamp, recordCollector);
			totalSpent = totalSpent.add(price.multiply(amount));
			totalAmount = totalAmount.add(amount);
		}
		clearTakerOrder(takerOrder, matchResult.takerStatus, totalSpent, totalAmount, timestamp);
		// batch insert:
		db.save(recordCollector.toArray(new MatchRecord[recordCollector.size()]));
	}

	void clearTakerOrder(Order takerOrder, OrderStatus status, BigDecimal totalSpent, BigDecimal totalAmount,
			long timestamp) {
		switch (takerOrder.type) {
		case BUY_LIMIT:
			// BTC++ and USD--
			accountService.frozenToSpot(takerOrder.userId, takerOrder.symbol.quote, totalSpent, takerOrder.symbol.base,
					totalAmount);
			break;
		case SELL_LIMIT:
			// BTC-- and USD++
			accountService.frozenToSpot(takerOrder.userId, takerOrder.symbol.base, totalAmount, takerOrder.symbol.quote,
					totalSpent);
			break;
		case BUY_MARKET:
		case SELL_MARKET:
		case CANCEL_BUY_LIMIT:
		case CANCEL_SELL_LIMIT:
		default:
			throw new RuntimeException("Not implemented order type: " + takerOrder.type);
		}
	}

	void clearMakerOrder(Order takerOrder, Order makerOrder, BigDecimal price, BigDecimal amount, OrderStatus status,
			long timestamp, List<MatchRecord> recordCollector) {
		switch (makerOrder.type) {
		case BUY_LIMIT:
			// BTC++ and USD--
			accountService.frozenToSpot(makerOrder.userId, makerOrder.symbol.quote, price.multiply(amount),
					makerOrder.symbol.base, amount);
			makerOrder.status = status;
			makerOrder.filledAmount = makerOrder.filledAmount.add(amount);
			db.updateProperties(makerOrder, "status", "filledAmount", "updatedAt", "version");
			recordCollector.add(createOrderMatchRecord(makerOrder.id, MatchType.MAKER, price, amount, timestamp));
			recordCollector.add(createOrderMatchRecord(takerOrder.id, MatchType.TAKER, price, amount, timestamp));
			break;
		case SELL_LIMIT:
			// BTC-- and USD++
			accountService.frozenToSpot(makerOrder.userId, makerOrder.symbol.base, amount, makerOrder.symbol.quote,
					price.multiply(amount));
			makerOrder.status = status;
			makerOrder.filledAmount = makerOrder.filledAmount.add(amount);
			db.updateProperties(makerOrder, "status", "filledAmount", "updatedAt", "version");
			recordCollector.add(createOrderMatchRecord(makerOrder.id, MatchType.MAKER, price, amount, timestamp));
			recordCollector.add(createOrderMatchRecord(takerOrder.id, MatchType.TAKER, price, amount, timestamp));
			break;
		case BUY_MARKET:
		case SELL_MARKET:
		default:
			throw new RuntimeException("Not implemented order type: " + makerOrder.type);
		}
	}

	MatchRecord createOrderMatchRecord(long orderId, MatchType type, BigDecimal price, BigDecimal amount,
			long timestamp) {
		MatchRecord record = new MatchRecord();
		record.orderId = orderId;
		record.matchType = type;
		record.matchPrice = price;
		record.matchAmount = amount;
		record.matchedAt = timestamp;
		return record;
	}

	public void processCancelled(CancelledMessage msg) {
		// TODO Auto-generated method stub

	}

}
