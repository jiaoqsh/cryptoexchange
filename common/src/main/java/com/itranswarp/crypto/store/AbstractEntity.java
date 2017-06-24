package com.itranswarp.crypto.store;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Base entity class.
 * 
 * @author liaoxuefeng
 */
@MappedSuperclass
public class AbstractEntity implements Serializable {

	/**
	 * Primary key: auto-increment long.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	public long id;

	/**
	 * Created time (milliseconds).
	 */
	@Column(nullable = false, updatable = false)
	public long createdAt;

	/**
	 * Updated time (milliseconds).
	 */
	@Column(nullable = false)
	public long updatedAt;

	/**
	 * Entity version: increment when update.
	 */
	@Column(nullable = false)
	public long version;

	// hook for pre-insert:
	@PrePersist
	void preInsert() {
		this.createdAt = this.updatedAt = System.currentTimeMillis();
		this.version = 0;
	}

	// hook for pre-update:
	@PreUpdate
	void preUpdate() {
		this.updatedAt = System.currentTimeMillis();
		this.version++;
	}
}
