package com.itranswarp.crypto.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.match.DepthSnapshot;
import com.itranswarp.crypto.symbol.Symbol;

@RestController
@RequestMapping("/rest/api/market")
public class RestMarketApiController extends AbstractApiController {

	@GetMapping("/depth")
	public DepthSnapshot depth(Symbol symbol) {
		return matchService.getDepthSnapshot();
	}

}
