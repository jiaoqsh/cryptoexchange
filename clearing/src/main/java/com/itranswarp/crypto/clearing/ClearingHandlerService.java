package com.itranswarp.crypto.clearing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.enums.MatchType;
import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.match.MatchRecord;
import com.itranswarp.crypto.match.MatchResult;
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
	public void processMatchResult(MatchResult matchResult) {
		final long timestamp = matchResult.timestamp;
		// a match result contains one taker and one or more makers:
		Order takerOrder = null;
		List<OrderMatchRecord> recordCollector = new ArrayList<>();
		BigDecimal totalSpent = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (MatchRecord record : matchResult.getMatchRecords()) {
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
		db.save(recordCollector.toArray(new OrderMatchRecord[recordCollector.size()]));
	}

	void clearTakerOrder(Order takerOrder, OrderStatus status, BigDecimal totalSpent, BigDecimal totalAmount,
			long timestamp) {
		switch (takerOrder.type) {
		case BUY_LIMIT:
			// BTC++ and CNY--
			accountService.frozenToSpot(takerOrder.userId, takerOrder.quoteCurrency, totalSpent,
					takerOrder.baseCurrency, totalAmount);
			break;
		case SELL_LIMIT:
			// BTC-- and CNY++
			accountService.frozenToSpot(takerOrder.userId, takerOrder.baseCurrency, totalAmount,
					takerOrder.quoteCurrency, totalSpent);
			break;
		case BUY_MARKET:
		case SELL_MARKET:
		case BUY_CANCEL:
		case SELL_CANCEL:
		default:
			throw new RuntimeException("Not implemented order type: " + takerOrder.type);
		}
	}

	void clearMakerOrder(Order takerOrder, Order makerOrder, BigDecimal price, BigDecimal amount, OrderStatus status,
			long timestamp, List<OrderMatchRecord> recordCollector) {
		switch (makerOrder.type) {
		case BUY_LIMIT:
			// BTC++ and CNY--
			accountService.frozenToSpot(makerOrder.userId, makerOrder.quoteCurrency, price.multiply(amount),
					makerOrder.baseCurrency, amount);
			makerOrder.status = status;
			makerOrder.filledAmount = makerOrder.filledAmount.add(amount);
			db.updateProperties(makerOrder, "status", "filledAmount", "updatedAt", "version");
			recordCollector.add(createOrderMatchRecord(makerOrder.id, MatchType.MAKER, price, amount, timestamp));
			recordCollector.add(createOrderMatchRecord(takerOrder.id, MatchType.TAKER, price, amount, timestamp));
			break;
		case SELL_LIMIT:
			// BTC-- and CNY++
			accountService.frozenToSpot(makerOrder.userId, makerOrder.baseCurrency, amount, makerOrder.quoteCurrency,
					price.multiply(amount));
			makerOrder.status = status;
			makerOrder.filledAmount = makerOrder.filledAmount.add(amount);
			db.updateProperties(makerOrder, "status", "filledAmount", "updatedAt", "version");
			recordCollector.add(createOrderMatchRecord(makerOrder.id, MatchType.MAKER, price, amount, timestamp));
			recordCollector.add(createOrderMatchRecord(takerOrder.id, MatchType.TAKER, price, amount, timestamp));
			break;
		case BUY_MARKET:
		case SELL_MARKET:
		case BUY_CANCEL:
		case SELL_CANCEL:
		default:
			throw new RuntimeException("Not implemented order type: " + makerOrder.type);
		}
	}

	OrderMatchRecord createOrderMatchRecord(long orderId, MatchType type, BigDecimal price, BigDecimal amount,
			long timestamp) {
		OrderMatchRecord record = new OrderMatchRecord();
		record.orderId = orderId;
		record.matchType = type;
		record.matchPrice = price;
		record.matchAmount = amount;
		record.matchedAt = timestamp;
		return record;
	}

}
