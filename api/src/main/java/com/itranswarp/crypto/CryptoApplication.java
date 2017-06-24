package com.itranswarp.crypto;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itranswarp.crypto.match.Tick;
import com.itranswarp.crypto.order.Order;
import com.itranswarp.crypto.queue.MessageQueue;
import com.itranswarp.warpdb.WarpDb;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class CryptoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}

	@Value("${crypto.basePackage:com.itranswarp.crypto}")
	String basePackage;

	@Autowired
	DataSource dataSource;

	@Bean
	WarpDb createWarpDb() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(basePackage));
		return db;
	}

	@Bean("orderMessageQueue")
	MessageQueue<Order> createOrderMessageQueue() {
		return new MessageQueue<>(10000);
	}

	@Bean("tickMessageQueue")
	MessageQueue<Tick> createTickMessageQueue() {
		return new MessageQueue<>(10000);
	}

	@Bean
	public Docket createSwaggerDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.globalOperationParameters(
						Arrays.asList(new ParameterBuilder().name("Authorization").description("Basic authorization")
								.modelRef(new ModelRef("string")).parameterType("header").required(false).build()))
				.select().paths(PathSelectors.ant("/api/**")).build();
	}

}
