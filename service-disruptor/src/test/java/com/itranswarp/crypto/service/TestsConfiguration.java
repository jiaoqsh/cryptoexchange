package com.itranswarp.crypto.service;

import com.itranswarp.crypto.match.TickMessage;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.warpdb.WarpDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

/**
 * @file: TestsConfiguration
 * @author: jiaoqsh
 * @since: 2018/02/02
 */
@SpringBootApplication
@ComponentScan("com.itranswarp.crypto")
public class TestsConfiguration {

    @Value("${crypto.basePackage:com.itranswarp.crypto}")
    String basePackage;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Bean
    WarpDb createWarpDb() {
        WarpDb db = new WarpDb();
        db.setJdbcTemplate(jdbcTemplate);
        db.setBasePackages(Arrays.asList(basePackage));
        return db;
    }

    @Bean("orderSequenceQueue")
    MessageQueue<Order> createOrderSequenceQueue() {
        return new MessageQueue<>(10000);
    }

    @Bean("orderMessageQueue")
    MessageQueue<OrderMessage> createOrderMessageQueue() {
        return new MessageQueue<>(10000);
    }

    @Bean("matchMessageQueue")
    MessageQueue<MatchMessage> createMatchMessageQueue() {
        return new MessageQueue<>(10000);
    }

    @Bean("tickMessageQueue")
    MessageQueue<TickMessage> createTickMessageQueue() {
        return new MessageQueue<>(10000);
    }
}
