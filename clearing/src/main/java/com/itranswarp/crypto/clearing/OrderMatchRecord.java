package com.itranswarp.crypto.clearing;

import java.math.BigDecimal;

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
@Table(indexes = @Index(name = "IDX_ORDERID", columnList = "orderId"))
public class OrderMatchRecord extends AbstractEntity {

	/**
	 * Order id.
	 */
	public long orderId;

	/**
	 * Match type.
	 */
	public MatchType matchType;

	/**
	 * The match price.
	 */
	public BigDecimal matchPrice;

	/**
	 * The match amount.
	 */
	public BigDecimal matchAmount;

	/**
	 * The match time.
	 */
	public long matchedAt;

}
