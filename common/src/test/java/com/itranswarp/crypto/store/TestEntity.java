package com.itranswarp.crypto.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.itranswarp.crypto.store.AbstractEntity;

@Entity
@Table(name = "ttt")
public class TestEntity extends AbstractEntity {

	@Column(name = "gid", nullable = false)
	public long groupId;

	@Column(nullable = false, length = 100)
	public String name;

	@Column(columnDefinition = "text")
	public String content;
}
