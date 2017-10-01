package com.itranswarp.crypto.clearing;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.itranswarp.crypto.enums.MatchType;
import com.itranswarp.crypto.store.AbstractEntity;

/**
 * Store the match details for each order.
 * 
 * @author liaoxuefeng
 */
@Entity
@Table(name = "match_records", indexes = @Index(name = "IDX_ORDERID", columnList = "orderId"))
public class MatchRecord extends AbstractEntity {

	/**
	 * Order id.
	 */
	@Column(nullable = false, updatable = false)
	public long orderId;

	/**
	 * Match type.
	 */
	@Column(length = VAR_ENUM, nullable = false, updatable = false)
	public MatchType matchType;

	/**
	 * The match price.
	 */
	@Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
	public BigDecimal matchPrice;

	/**
	 * The match amount.
	 */
	@Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
	public BigDecimal matchAmount;

	/**
	 * The match time.
	 */
	@Column(nullable = false, updatable = false)
	public long matchedAt;

}
