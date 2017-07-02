package com.itranswarp.crypto.clearing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.match.MatchResult;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractRunnableService;

/**
 * Process match result and update maker and taker's accounts.
 * 
 * @author liaoxuefeng
 */
@Component
public class ClearingService extends AbstractRunnableService {

	@Autowired
	ClearingHandlerService clearingHandler;

	final MessageQueue<MatchResult> matchResultMessageQueue;

	public ClearingService(
			@Autowired @Qualifier("matchResultMessageQueue") MessageQueue<MatchResult> matchResultMessageQueue) {
		this.matchResultMessageQueue = matchResultMessageQueue;
	}

	void processMatchResult(MatchResult matchResult) {
		clearingHandler.processMatchResult(matchResult);
	}

	@Override
	protected void process() throws InterruptedException {
		while (true) {
			MatchResult matchResult = matchResultMessageQueue.getMessage();
			processMatchResult(matchResult);
		}
	}

	@Override
	protected void clean() throws InterruptedException {
		while (true) {
			MatchResult matchResult = matchResultMessageQueue.getMessage(10);
			if (matchResult != null) {
				processMatchResult(matchResult);
			} else {
				break;
			}
		}
	}
}
