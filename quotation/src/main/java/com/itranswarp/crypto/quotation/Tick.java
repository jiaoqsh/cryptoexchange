package com.itranswarp.crypto.quotation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
@Table(name = "tick")
public class TickEntity extends AbstractEntity {

	@Column(length = 20, nullable = false)
	public String symbol;

	@Column(unique = true, nullable = false)
	public long time;

	@Column(nullable = false)
	public long price;

	@Column(nullable = false)
	public long amount;

}
