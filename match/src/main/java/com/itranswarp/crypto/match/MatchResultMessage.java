package com.itranswarp.crypto.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.itranswarp.crypto.enums.OrderStatus;

/**
 * A lazy-initialized list contains match records.
 * 
 * @author liaoxuefeng
 */
public class MatchResultMessage {

	public OrderStatus takerStatus;
	public final long timestamp;

	private List<MatchRecordMessage> matchRecords = null;

	public MatchResultMessage(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<MatchRecordMessage> getMatchRecords() {
		return matchRecords == null ? Collections.emptyList() : matchRecords;
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
		List<MatchRecordMessage> records = getMatchRecords();
		StringBuilder sb = new StringBuilder(128).append("MatchResult: taker status=").append(this.takerStatus)
				.append(", records=").append(records.size())
				.append("\n----------------------------------------------------------------------\n");
		for (MatchRecordMessage record : records) {
			sb.append(record).append("\n");
		}
		return sb.append("----------------------------------------------------------------------").toString();
	}
}
