package com.itranswarp.crypto;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itranswarp.crypto.match.TickMessage;
import com.itranswarp.crypto.match.message.MatchMessage;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.crypto.sequence.message.OrderMessage;
import com.itranswarp.warpdb.WarpDb;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class CryptoExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoExchangeApplication.class, args);
	}

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

	/**
	 * Init swagger for quick API debugging.
	 * 
	 * @return Swagger Docket.
	 */
	@Bean
	public Docket createSwaggerDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.globalOperationParameters(
						Arrays.asList(new ParameterBuilder().name("Authorization").modelRef(new ModelRef("string"))
								.parameterType("header").required(false).description("Basic authorization")
								// test@example.com:7c4a8d09ca3762af61e59520943dc26494f8941b
								.defaultValue(
										"Basic dGVzdEBleGFtcGxlLmNvbTo3YzRhOGQwOWNhMzc2MmFmNjFlNTk1MjA5NDNkYzI2NDk0Zjg5NDFi")
								.build()))
				.select().paths(PathSelectors.regex("^/(rest|manage)/api/.*$")).build();
	}

}
