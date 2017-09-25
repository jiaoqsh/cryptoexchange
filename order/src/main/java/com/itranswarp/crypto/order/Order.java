package com.itranswarp.crypto.order;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.itranswarp.crypto.enums.OrderStatus;
import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.store.AbstractEntity;
import com.itranswarp.crypto.symbol.Symbol;

/**
 * Order entity.
 * 
 * @author liaoxuefeng
 */
@Entity
@Table(name = "orders", indexes = { @Index(name = "IDX_USERID_STATUS", columnList = "userId, status"),
		@Index(name = "IDX_CREATEDAT", columnList = "createdAt") })
public class Order extends AbstractEntity {

	@Column(nullable = false, updatable = false)
	public long userId;

	@Column(nullable = false, updatable = false)
	public Symbol symbol;

	@Column(nullable = false, updatable = false)
	public OrderType type;

	@Column(nullable = false, updatable = false)
	public BigDecimal price;

	@Column(nullable = false, updatable = false)
	public BigDecimal amount;

	@Column(nullable = false)
	public BigDecimal filledAmount;

	@Column(nullable = false)
	public OrderStatus status;
}
