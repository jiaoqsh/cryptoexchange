package com.itranswarp.crypto.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.api.bean.OrderBean;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.user.User;
import com.itranswarp.crypto.user.UserContext;

@RestController
@RequestMapping("/rest/api/order")
public class RestOrderApiController extends AbstractApiController {

	/**
	 * Create a new order.
	 * 
	 * @param symbol
	 * @param orderType
	 * @param price
	 * @param amount
	 * @return Order object.
	 * @throws InterruptedException
	 */
	@PostMapping("/orders")
	public Order createOrder(@RequestBody OrderBean order) throws InterruptedException {
		User user = UserContext.getRequiredCurrentUser();
		switch (order.orderType) {
		case BUY_LIMIT:
			return orderService.createBuyLimitOrder(user.id, order.symbol, order.price, order.amount);
		case SELL_LIMIT:
			return orderService.createSellLimitOrder(user.id, order.symbol, order.price, order.amount);
		default:
			throw new ApiException(ApiError.ORDER_INVALID, "orderType", "Unsupported order type.");
		}
	}

}
