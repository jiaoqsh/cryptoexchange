package com.itranswarp.crypto.sequence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.store.AbstractService;

@Component
@Transactional
public class SequenceHandler extends AbstractService {

	public void doSequenceOrder(long seqId, Order order) {
		final long ts = System.currentTimeMillis();
		order.status = OrderStatus.SEQUENCED;
		order.updatedAt = ts;
		OrderSequence seq = new OrderSequence();
		seq.id = seqId;
		seq.orderId = order.id;
		seq.createdAt = ts;
		db.updateProperties(order, "status", "updatedAt");
		db.save(seq);
	}

}
