package com.itranswarp.crypto.quotation;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.itranswarp.crypto.store.AbstractEntity;
import com.itranswarp.crypto.symbol.Symbol;

@Entity
@Table(name = "ticks")
public class Tick extends AbstractEntity {

	@Column(length = 20, nullable = false, updatable = false)
	public Symbol symbol;

	@Column(unique = true, nullable = false, updatable = false)
	public long time;

	@Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
	public long price;

	@Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
	public BigDecimal amount;

	@Override
	public String toString() {
		return String.format("Tick(symbol=%s, time=%d, price=%.2f, amount=%.4f)", symbol.name(), time, price, amount);
	}
}
