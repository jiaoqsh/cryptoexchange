package com.itranswarp.crypto.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A lazy-initialized list contains match records.
 * 
 * @author liaoxuefeng
 */
public class MatchResult {

	public final long timestamp;

	private List<MatchRecord> matchRecords = null;

	public MatchResult(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<MatchRecord> getMatchRecords() {
		return matchRecords == null ? Collections.emptyList() : matchRecords;
	}

	public void addMatchRecord(MatchRecord matchRecord) {
		if (matchRecords == null) {
			matchRecords = new ArrayList<>();
		}
		matchRecords.add(matchRecord);
	}

	public boolean isEmpty() {
		return matchRecords == null;
	}

}
