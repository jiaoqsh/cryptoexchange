package com.itranswarp.crypto.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "email" }))
public class User extends AbstractEntity {

	public static enum UserType {
		PENDING, NORMAL
	}

	@Column(nullable = false, length = 20)
	public UserType type;

	@Column(nullable = false, updatable = false, length = 50)
	public String email;

	@Column(nullable = false, length = 50)
	public String name;

}
