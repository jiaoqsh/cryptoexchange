package com.itranswarp.crypto.clearing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.match.message.CancelledMessage;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.match.message.MatchResultMessage;
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
	@Qualifier("matchMessageQueue")
	MessageQueue<MatchMessage> matchMessageQueue;

	@Override
	protected void process() throws InterruptedException {
		while (true) {
			MatchMessage msg = matchMessageQueue.getMessage();
			processMatchMessage(msg);
		}
	}

	@Override
	protected void clean() throws InterruptedException {
		while (true) {
			MatchMessage msg = matchMessageQueue.getMessage(10);
			if (msg != null) {
				processMatchMessage(msg);
			} else {
				break;
			}
		}
	}

	void processMatchMessage(MatchMessage msg) {
		logger.info("Process match message:\n" + msg);
		if (msg instanceof MatchResultMessage) {
			clearingHandler.processMatched((MatchResultMessage) msg);
		} else if (msg instanceof CancelledMessage) {
			clearingHandler.processCancelled((CancelledMessage) msg);
		} else {
			logger.error("Invalid MatchMessage type: " + msg.getClass().getName());
		}
	}

}
