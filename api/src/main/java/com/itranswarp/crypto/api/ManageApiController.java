package com.itranswarp.crypto.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.account.SpotAccount;
import com.itranswarp.crypto.symbol.Currency;
import com.itranswarp.crypto.user.User;

@RestController
@RequestMapping("/manage/api")
public class ManageApiController extends AbstractApiController {

	@GetMapping("/users")
	public Map<String, List<User>> getUsers(@RequestParam(defaultValue = "0") long startId,
			@RequestParam(defaultValue = "20") int maxResults) {
		List<User> users = userService.findUsers(startId, maxResults);
		return SimpleResponse.of("users", users);
	}

	@PostMapping("/danger/deposit")
	public SpotAccount deposit(long userId, Currency currency, BigDecimal amount) {
		accountService.deposit(userId, currency, amount);
		return accountService.getSpotAccount(userId, currency);
	}

}
