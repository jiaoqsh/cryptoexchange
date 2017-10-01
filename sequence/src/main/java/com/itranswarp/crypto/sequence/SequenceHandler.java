package com.itranswarp.crypto.sequence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.store.AbstractService;

@Component
@Transactional
public class SequenceHandler extends AbstractService {

	public void doSequenceOrder(final long seqId, Order order) {
		final long ts = System.currentTimeMillis();
		order.status = OrderStatus.SEQUENCED;
		order.seqId = seqId;
		order.updatedAt = ts;
		OrderSequence seq = new OrderSequence();
		seq.id = seqId;
		seq.orderId = order.id;
		seq.createdAt = ts;
		db.updateProperties(order, "status", "seqId", "updatedAt");
		db.save(seq);
	}

}
