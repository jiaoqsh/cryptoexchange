package com.itranswarp.crypto.store;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
public class UserEntity extends AbstractEntity {

	@Column(nullable = false, length = 20)
	public String type;

	@Column(nullable = false, length = 50)
	public String name;

}
