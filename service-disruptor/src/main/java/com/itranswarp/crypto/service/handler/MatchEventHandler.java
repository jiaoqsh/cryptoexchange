package com.itranswarp.crypto.service.handler;

import com.itranswarp.crypto.match.MatchService;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.crypto.service.model.OrderEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @file: MatchEventHandler
 * @author: jiaoqsh
 * @since: 2018/02/02
 */
@Component
public class MatchEventHandler implements EventHandler<OrderEvent> {

    private Logger logger = LoggerFactory.getLogger(MatchEventHandler.class);

    @Autowired
    private MatchService matchService;

    @Override
    public void onEvent(OrderEvent orderEvent, long sequence, boolean endOfBatch) throws Exception {
        logger.info("sequence={}|orderEvent={}", sequence, orderEvent);
        OrderMessage orderMessage = orderEvent.getOrderMessage();
        matchService.processOrder(orderMessage);
    }
}
