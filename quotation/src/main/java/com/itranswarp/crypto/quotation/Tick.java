package com.itranswarp.crypto.quotation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.itranswarp.crypto.store.AbstractEntity;
import com.itranswarp.crypto.symbol.Symbol;

@Entity
@Table(name = "ticks")
public class Tick extends AbstractEntity {

	@Column(length = 20, nullable = false)
	public Symbol symbol;

	@Column(unique = true, nullable = false)
	public long time;

	@Column(nullable = false)
	public long price;

	@Column(nullable = false)
	public long amount;

	@Override
	public String toString() {
		return String.format("Tick(symbol=%s, time=%d, price=%.2f, amount=%.4f)", symbol.name(), time, price, amount);
	}
}
