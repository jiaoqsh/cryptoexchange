package com.itranswarp.crypto.service.producer;

import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.service.handler.MatchEventHandler;
import com.itranswarp.crypto.service.handler.SequenceEventHandler;
import com.itranswarp.crypto.service.model.OrderEvent;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @file: OrderEventProducer
 * @author: jiaoqsh
 * @since: 2018/02/02
 */
@Component
public class OrderEventProducer {

    private Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);

    private Disruptor<OrderEvent> disruptor;

    private RingBuffer<OrderEvent> ringBuffer;

    @Autowired
    private SequenceEventHandler sequenceEventHandler;

    @Autowired
    private MatchEventHandler matchEventHandler;

    private static final EventTranslatorOneArg<OrderEvent, Order> TRANSLATOR = (event, sequence, order) -> event.setOrder(order);

    @PostConstruct
    public void start() {
        // 指明RingBuffer的大小，必须为2的幂
        int bufferSize = 1024;
        disruptor = new Disruptor(() -> new OrderEvent(), bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        // 置入处理逻辑
        disruptor.handleEventsWith(sequenceEventHandler).then(matchEventHandler);

        disruptor.start();
        logger.info("disruptor start...");

        // 获取ringBuffer，用于发布事件
        ringBuffer = disruptor.getRingBuffer();
    }

    @PreDestroy
    public void shutdown() {
        disruptor.shutdown();
        logger.info("disruptor shutdown...");
    }

    public void onData(Order order) {
        ringBuffer.publishEvent(TRANSLATOR, order);
    }
}
