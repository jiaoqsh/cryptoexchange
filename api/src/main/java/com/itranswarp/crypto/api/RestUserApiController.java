package com.itranswarp.crypto.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.account.SpotAccount;
import com.itranswarp.crypto.api.bean.UserBean;
import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.symbol.Symbol;
import com.itranswarp.crypto.user.User;
import com.itranswarp.crypto.user.UserContext;

@RestController
@RequestMapping("/rest/api/user")
public class RestUserApiController extends AbstractApiController {

	@PostMapping("/register")
	public User createUser(@RequestBody UserBean user) {
		return userService.createUser(user.email, user.passwd, user.name);
	}

	@GetMapping("/accounts")
	public Map<String, List<SpotAccount>> getAccounts() {
		List<SpotAccount> list = accountService.getSpotAccounts(UserContext.getRequiredCurrentUser().id);
		return SimpleResponse.of("spotAccounts", list);
	}

	public Order createOrder(Symbol symbol, OrderType orderType, BigDecimal price, BigDecimal amount) {
		return null;
	}

}
