package com.itranswarp.crypto.order;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.itranswarp.crypto.store.AbstractEntity;
import com.itranswarp.crypto.symbol.Currency;

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
	public Currency baseCurrency;

	@Column(nullable = false, updatable = false)
	public Currency quoteCurrency;

	@Column(nullable = false, updatable = false)
	public OrderType type;

	@Column(nullable = false, updatable = false)
	public BigDecimal price;

	@Column(nullable = false, updatable = false)
	public BigDecimal amount;

	@Column(nullable = false)
	public OrderStatus status;
}
