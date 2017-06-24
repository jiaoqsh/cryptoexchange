package com.itranswarp.crypto.store;

import org.springframework.beans.factory.annotation.Autowired;

import com.itranswarp.warpdb.WarpDb;

public class DbTestBase {

	@Autowired
	WarpDb db;

	public void createTable(Class<?> clazz) {
		String ddl = this.db.getDDL(clazz);
		this.db.update("DROP TABLE IF EXISTS " + clazz.getSimpleName());
		this.db.update(ddl);
	}
}
