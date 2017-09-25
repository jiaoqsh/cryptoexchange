package com.itranswarp.crypto.sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Order sequence entity.
 * 
 * @author liaoxuefeng
 */
@Entity
@Table(name = "order_sequences")
public class OrderSequence {

	/**
	 * Primary key: sequence id.
	 */
	@Id
	@Column(nullable = false, updatable = false)
	public long id;

	@Column(nullable = false, updatable = false)
	public long orderId;

	/**
	 * Created time (milliseconds).
	 */
	@Column(nullable = false, updatable = false)
	public long createdAt;

}
