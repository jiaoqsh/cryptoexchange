package com.itranswarp.crypto.clearing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.match.MatchResult;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractRunnableService;

@Component
public class ClearingService extends AbstractRunnableService {

	final MessageQueue<MatchResult> matchResultQueue;

	public ClearingService(@Autowired @Qualifier("matchResultQueue") MessageQueue<MatchResult> matchResultQueue) {
		this.matchResultQueue = matchResultQueue;
	}

	@Override
	protected void process() throws InterruptedException {
	}

	@Override
	protected void clean() throws InterruptedException {
	}
}
