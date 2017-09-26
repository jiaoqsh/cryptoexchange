package com.itranswarp.crypto.clearing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.match.MatchResultMessage;
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

	@Autowired
	@Qualifier("matchResultMessageQueue")
	MessageQueue<MatchResultMessage> matchResultMessageQueue;

	@Override
	protected void process() throws InterruptedException {
		while (true) {
			MatchResultMessage matchResult = matchResultMessageQueue.getMessage();
			processMatchResult(matchResult);
		}
	}

	@Override
	protected void clean() throws InterruptedException {
		while (true) {
			MatchResultMessage matchResult = matchResultMessageQueue.getMessage(10);
			if (matchResult != null) {
				processMatchResult(matchResult);
			} else {
				break;
			}
		}
	}

	void processMatchResult(MatchResultMessage matchResult) {
		logger.info("Process match result:\n" + matchResult);
		clearingHandler.processMatchResult(matchResult);
	}

}
