package com.itranswarp.crypto.store;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itranswarp.warpdb.WarpDb;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Hold configuration for database.
 * 
 * @author liaoxuefeng
 */
@Configuration
public class DbTestConfiguration {

	@Value("${spring.datasource.url}")
	String url;

	@Value("${spring.datasource.username}")
	String username;

	@Value("${spring.datasource.password}")
	String password;

	@Value("${spring.datasource.hikari.poolName:HikariCP}")
	String poolName;

	@Value("${spring.datasource.hikari.connectionTimeout:5000}")
	String connectionTimeout;

	@Value("${crypto.basePackage:com.itranswarp.crypto}")
	String basePackage;

	@Bean
	public DataSource createDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(this.url);
		config.setUsername(this.username);
		config.setPassword(this.password);
		config.addDataSourceProperty("poolName", this.poolName);
		config.addDataSourceProperty("connectionTimeout", this.connectionTimeout);
		config.addDataSourceProperty("autoCommit", "false");
		return new HikariDataSource(config);
	}

	@Autowired
	DataSource dataSource;

	@Bean
	public JdbcTemplate createJdbcTemplate() {
		return new JdbcTemplate(this.dataSource);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Bean
	public WarpDb createWarpDb() {
		WarpDb warpdb = new WarpDb();
		warpdb.setBasePackages(Arrays.asList(basePackage));
		warpdb.setJdbcTemplate(jdbcTemplate);
		warpdb.init();
		return warpdb;
	}
}
