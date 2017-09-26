package com.itranswarp.crypto.account;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.store.AbstractEntity;
import com.itranswarp.crypto.symbol.Currency;

@Entity
@Table(name = "spot_accounts", uniqueConstraints = @UniqueConstraint(name = "UNI_USERID_CURRENCY", columnNames = {
		"userId", "currency" }))
public class SpotAccount extends AbstractEntity {

	@Column(nullable = false)
	public long userId;

	@Column(nullable = false, length = 20)
	public Currency currency;

	@Column(nullable = false, precision = PRECISION, scale = SCALE)
	public BigDecimal balance;

	@Column(nullable = false, precision = PRECISION, scale = SCALE)
	public BigDecimal frozen;

	@Override
	public String toString() {
		return String.format("SpotAccount(userId=%s, %s=%s, %s)", userId, currency.name(), currency.display(balance),
				currency.display(frozen));
	}
}
