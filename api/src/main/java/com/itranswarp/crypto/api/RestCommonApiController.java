package com.itranswarp.crypto.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.symbol.Currency;
import com.itranswarp.crypto.symbol.Symbol;

@RestController
@RequestMapping("/rest/api/common")
public class RestCommonApiController extends AbstractApiController {

	@GetMapping("/timestamp")
	public Map<String, Long> timestamp() {
		return SimpleResponse.of("timestamp", System.currentTimeMillis());
	}

	@GetMapping("/symbols")
	public Map<String, Symbol[]> symbols() {
		return SimpleResponse.of("symbols", Symbol.values());
	}

	@GetMapping("/currencies")
	public Map<String, Currency[]> currencies() {
		return SimpleResponse.of("currencies", Currency.values());
	}

}
