package com.itranswarp.crypto.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.account.AccountService;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.order.OrderService;
import com.itranswarp.crypto.order.OrderType;
import com.itranswarp.crypto.symbol.Symbol;
import com.itranswarp.crypto.user.UserService;

@RestController
@RequestMapping("/api")
public class RestApiController {

	@Autowired
	UserService userService;

	@Autowired
	AccountService accountService;

	@Autowired
	OrderService orderService;

	@GetMapping("/timestamp")
	public SimpleResponse<Long> timestamp() {
		return SimpleResponse.of(System.currentTimeMillis());
	}

	public Order createOrder(Symbol symbol, OrderType orderType, long price, long amount) {
		return null;
	}

}
