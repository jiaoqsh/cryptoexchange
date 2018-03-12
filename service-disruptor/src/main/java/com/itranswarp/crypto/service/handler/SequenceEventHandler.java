package com.itranswarp.crypto.service.handler;

import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.sequence.SequenceHandler;
import com.itranswarp.crypto.sequence.SequenceService;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.crypto.service.model.OrderEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @file: SequenceEventHandler
 * @author: jiaoqsh
 * @since: 2018/02/02
 */
@Component
public class SequenceEventHandler implements EventHandler<OrderEvent> {

    private Logger logger = LoggerFactory.getLogger(SequenceEventHandler.class);

    @Autowired
    private SequenceHandler sequenceHandler;

    @Autowired
    private SequenceService sequenceService;

    @Override
    public void onEvent(OrderEvent orderEvent, long sequence, boolean endOfBatch) throws Exception {
        logger.info("sequence={}|orderEvent={}", sequence, orderEvent);
        Order order = orderEvent.getOrder();
        long seqId = order.id;
        sequenceHandler.doSequenceOrder(seqId, order);
        OrderMessage orderMessage = sequenceService.createOrderMessage(sequence, order);
        orderEvent.setOrderMessage(orderMessage);
    }
}
