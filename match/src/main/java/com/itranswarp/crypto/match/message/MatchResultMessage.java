package com.itranswarp.crypto.match.message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.match.MatchRecordMessage;

/**
 * A lazy-initialized list contains match records.
 * 
 * @author liaoxuefeng
 */
public class MatchResultMessage implements MatchMessage {

	public OrderStatus takerStatus;
	
	/**
	 * How much the taker's amount left if market order.
	 */
	public BigDecimal takerAmount;
	public final long orderId;
	public final long timestamp;

	private List<MatchRecordMessage> matchRecords = null;

	public MatchResultMessage(long orderId, long timestamp) {
		this.orderId = orderId;
		this.timestamp = timestamp;
	}

	public boolean hasMatchRecord() {
		return matchRecords != null && !matchRecords.isEmpty();
	}

	public List<MatchRecordMessage> getMatchRecords() {
		return matchRecords;
	}

	public void addMatchRecord(MatchRecordMessage matchRecord) {
		if (matchRecords == null) {
			matchRecords = new ArrayList<>();
		}
		matchRecords.add(matchRecord);
	}

	public boolean isEmpty() {
		return matchRecords == null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(128).append("MatchResultMessage: taker status=").append(this.takerStatus)
				.append(", records=").append(this.matchRecords == null ? 0 : this.matchRecords.size())
				.append("\n----------------------------------------------------------------------\n");
		if (this.matchRecords != null) {
			for (MatchRecordMessage record : this.matchRecords) {
				sb.append(record).append("\n");
			}
		}
		return sb.append("----------------------------------------------------------------------").toString();
	}
}
