package com.itranswarp.crypto.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.store.AbstractService;

@Component
@Transactional
public class OrderHandlerService extends AbstractService {

	public void saveOrder(Order order) {
		db.save(order);
	}
}
