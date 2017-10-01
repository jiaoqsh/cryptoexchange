package com.itranswarp.crypto.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.api.bean.CancelOrderBean;
import com.itranswarp.crypto.api.bean.OrderBean;
import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.enums.OrderType;
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
			throw new ApiException(ApiError.PARAMETER_INVALID, "orderType", "Unsupported order type.");
		}
	}

	@PostMapping("/orders/{id}/cancel")
	public Order cancelOrder(@PathVariable("id") long orderId, @RequestBody CancelOrderBean cancelOrder)
			throws InterruptedException {
		User user = UserContext.getRequiredCurrentUser();
		Order orderToBeCancelled = orderService.getOrder(orderId);
		if (orderToBeCancelled.userId != user.id) {
			throw new ApiException(ApiError.ORDER_NOT_FOUND);
		}
		OrderType cancelType = CANCEL_TYPE_FROM.get(orderToBeCancelled.type);
		if (cancelType == null) {
			throw new ApiException(ApiError.ORDER_CANNOT_CANCEL,
					"CANNOT cancel order with type: " + orderToBeCancelled.type);
		}
		if (!CANCELLABLE_ORDER_STATUS.contains(orderToBeCancelled.status)) {
			throw new ApiException(ApiError.ORDER_CANNOT_CANCEL,
					"CANNOT cancel order with status: " + orderToBeCancelled.status);
		}
		return orderService.createCancelOrder(cancelType, orderToBeCancelled);
	}

	static final Set<OrderStatus> CANCELLABLE_ORDER_STATUS = new HashSet<>(
			Arrays.asList(OrderStatus.PARTIAL_FILLED, OrderStatus.SEQUENCED));

	static final Map<OrderType, OrderType> CANCEL_TYPE_FROM = createCancelTypeMap();

	private static Map<OrderType, OrderType> createCancelTypeMap() {
		Map<OrderType, OrderType> map = new HashMap<>();
		map.put(OrderType.BUY_LIMIT, OrderType.CANCEL_BUY_LIMIT);
		map.put(OrderType.SELL_LIMIT, OrderType.CANCEL_SELL_LIMIT);
		return map;
	}
}
