package com.itranswarp.crypto.clearing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "takerOrderId", "makerOrderId" }))
public class ClearingTransaction extends AbstractEntity {

	/**
	 * Taker's order id
	 */
	public long takerOrderId;

	/**
	 * Maker's order id
	 */
	public long makerOrderId;

	/**
	 * The match price.
	 */
	public long matchPrice;

	/**
	 * The match amount.
	 */
	public long matchAmount;

}
