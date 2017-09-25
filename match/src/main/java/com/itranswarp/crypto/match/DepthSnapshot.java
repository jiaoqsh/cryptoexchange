package com.itranswarp.crypto.match;

import java.util.List;

public class DepthSnapshot {

	public final long timestamp;

	public final List<OrderSnapshot> buyOrders;

	public final List<OrderSnapshot> sellOrders;

	public DepthSnapshot(long timestamp, List<OrderSnapshot> buyOrders, List<OrderSnapshot> sellOrders) {
		this.timestamp = timestamp;
		this.buyOrders = buyOrders;
		this.sellOrders = sellOrders;
	}

}
