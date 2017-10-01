package com.itranswarp.crypto.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.match.MatchService;
import com.itranswarp.crypto.order.OrderService;
import com.itranswarp.crypto.sequence.SequenceService;
import com.itranswarp.crypto.user.UserService;

/**
 * Base controller to autowire services.
 * 
 * @author liaoxuefeng
 */
public abstract class AbstractApiController {

	@Autowired
	protected UserService userService;

	@Autowired
	protected AccountService accountService;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected SequenceService sequenceService;

	@Autowired
	protected MatchService matchService;

}
