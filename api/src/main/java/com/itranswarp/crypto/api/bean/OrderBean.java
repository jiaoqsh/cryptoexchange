package com.itranswarp.crypto.api.bean;

import java.math.BigDecimal;

import com.itranswarp.crypto.enums.OrderType;
import com.itranswarp.crypto.symbol.Symbol;

public class OrderBean {

	public Symbol symbol;

	public OrderType orderType;

	public BigDecimal price;

	public BigDecimal amount;

}
