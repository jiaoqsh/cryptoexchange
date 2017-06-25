package com.itranswarp.crypto.clearing;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.match.MatchRecord;
import com.itranswarp.crypto.match.MatchResult;

@Component
@Transactional
public class ClearingHandler {

	public void processMatchResult(MatchResult matchResult) {
		for (MatchRecord record : matchResult.getMatchRecords()) {

		}

	}

}
