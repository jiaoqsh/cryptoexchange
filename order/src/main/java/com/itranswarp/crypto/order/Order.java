package com.itranswarp.crypto.order;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.itranswarp.crypto.store.AbstractEntity;

/**
 * Order entity.
 * 
 * @author liaoxuefeng
 */
@Entity
public class Order extends AbstractEntity {

	@Column(nullable = false, updatable = false)
	public long userId;

	@Column(nullable = false, updatable = false)
	public OrderType type;

	@Column(nullable = false, updatable = false)
	public long price;

	@Column(nullable = false, updatable = false)
	public long amount;

	@Column(nullable = false)
	public OrderStatus status;
}
