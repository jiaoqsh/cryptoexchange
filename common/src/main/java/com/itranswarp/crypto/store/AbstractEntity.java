package com.itranswarp.crypto.store;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Base entity class.
 * 
 * @author liaoxuefeng
 */
@MappedSuperclass
public class AbstractEntity implements Serializable {

	// default big decimal storage type: DECIMAL(PRECISION, SCALE)
	protected static final int PRECISION = 20;
	protected static final int SCALE = 8;
	protected static final int VAR_ENUM = 20;

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
	@JsonIgnore
	public long createdAt;

	/**
	 * Updated time (milliseconds).
	 */
	@Column(nullable = false)
	@JsonIgnore
	public long updatedAt;

	/**
	 * Entity version: increment when update.
	 */
	@Column(nullable = false)
	@JsonIgnore
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
