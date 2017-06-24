package com.itranswarp.crypto.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userId" }))
public class PasswordAuth extends AbstractEntity {

	@Column(nullable = false, updatable = false)
	public long userId;

	@Column(nullable = false, length = 50)
	public String passwd;
}
