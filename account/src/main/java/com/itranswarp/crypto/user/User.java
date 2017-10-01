package com.itranswarp.crypto.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "UNI_EMAIL", columnNames = { "email" }))
public class User extends AbstractEntity {

	public static enum UserType {
		PENDING, NORMAL
	}

	@Column(length = VAR_ENUM, nullable = false)
	public UserType type;

	@Column(nullable = false, updatable = false, length = 50)
	public String email;

	@Column(nullable = false, length = 50)
	public String name;

}
