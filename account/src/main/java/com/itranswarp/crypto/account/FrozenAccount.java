package com.itranswarp.crypto.account;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.store.AbstractEntity;
import com.itranswarp.crypto.symbol.Currency;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UNI_USERID_CURRENCY", columnNames = { "userId", "currency" }))
public class FrozenAccount extends AbstractEntity {

	@Column(nullable = false)
	public long userId;

	@Column(nullable = false, length = 20)
	public Currency currency;

	@Column(nullable = false, precision = 32, scale = 16)
	public BigDecimal balance;

	@Override
	public String toString() {
		return String.format("FrozenAccount(userId=%s, %s=%s)", userId, currency.name(), currency.display(balance));
	}
}
