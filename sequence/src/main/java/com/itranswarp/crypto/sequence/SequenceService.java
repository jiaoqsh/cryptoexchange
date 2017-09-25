package com.itranswarp.crypto.sequence;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.store.AbstractRunnableService;

@Component
public class SequenceService extends AbstractRunnableService {

	@Autowired
	@Qualifier("orderSequenceQueue")
	MessageQueue<Order> orderSequenceQueue;

	@Autowired
	MessageQueue<OrderMessage> orderMessageQueue;

	@Autowired
	SequenceHandler sequenceHandler;

	/**
	 * Sequence id.
	 */
	AtomicLong sequence;

	@Override
	protected void init() throws InterruptedException {
		// find max sequence id:
		logger.info("Find max sequence id...");
		OrderSequence maxSeq = db.from(OrderSequence.class).orderBy("id").desc().first();
		sequence = new AtomicLong(maxSeq == null ? 0 : maxSeq.id);
		logger.info("Max sequence id = " + sequence.get());

		// load all order message by sequence id:
		List<OrderSequence> seqs = db.from(OrderSequence.class).orderBy("id").list();
		logger.info("Resend " + seqs.size() + " order messages...");
		for (OrderSequence seq : seqs) {
			Order order = db.get(Order.class, seq.orderId);
			orderMessageQueue.sendMessage(new OrderMessage(seq.id, order));
		}

		// find SUBMITTED but not SEQUENCED orders:
		List<Order> orders = db.from(Order.class).where("status=?", OrderStatus.SUBMITTED).orderBy("id").list();
		logger.info("Do sequence " + orders.size() + " submitted orders...");
		for (Order order : orders) {
			orderSequenceQueue.sendMessage(order);
		}
	}

	@Override
	protected void process() throws InterruptedException {
		while (true) {
			Order order = orderSequenceQueue.getMessage();
			doSequenceOrder(sequence.incrementAndGet(), order);
		}
	}

	@Override
	protected void clean() throws InterruptedException {
		while (true) {
			Order order = orderSequenceQueue.getMessage(10);
			if (order != null) {
				doSequenceOrder(sequence.incrementAndGet(), order);
			} else {
				break;
			}
		}
	}

	void doSequenceOrder(final long seqId, Order order) throws InterruptedException {
		sequenceHandler.doSequenceOrder(seqId, order);
		orderMessageQueue.sendMessage(new OrderMessage(seqId, order));
	}
}
