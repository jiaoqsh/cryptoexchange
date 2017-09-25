package com.itranswarp.crypto.store;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import com.itranswarp.warpdb.WarpDb;

public class DbTestBase {

	@Autowired
	WarpDb db;

	public void createTable(Class<?> clazz) {
		String ddl = this.db.getDDL(clazz);
		this.db.update("DROP TABLE IF EXISTS " + getTableName(clazz));
		this.db.update(ddl);
	}

	String getTableName(Class<?> clazz) {
		Table t = clazz.getAnnotation(Table.class);
		if (t != null && !t.name().isEmpty()) {
			return t.name();
		}
		return clazz.getSimpleName();
	}
}
