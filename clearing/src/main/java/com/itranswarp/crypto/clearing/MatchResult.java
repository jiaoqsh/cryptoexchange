package com.itranswarp.crypto.clearing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.itranswarp.crypto.enums.MatchResultType;
import com.itranswarp.crypto.store.AbstractEntity;

/**
 * Store the match result in order to prevent multiple processing.
 * 
 * @author liaoxuefeng
 */
@Entity
@Table(name = "match_results", uniqueConstraints = @UniqueConstraint(name = "UNI_TYPE_ORDER_ID", columnNames = { "type",
		"orderId" }))
public class MatchResult extends AbstractEntity {

	/**
	 * Match result type.
	 */
	@Column(length = VAR_ENUM, nullable = false, updatable = false)
	MatchResultType type;

	/**
	 * The target's order id. If type is MATCHED, the order id is taker's order
	 * id. If type is CANCELLED, the order id is the cancelled order id.
	 */
	@Column(nullable = false, updatable = false)
	public long orderId;

}
