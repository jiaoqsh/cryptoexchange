package com.itranswarp.crypto;

import java.util.Arrays;

import com.itranswarp.warpdb.WarpDb;

public class SchemaBuilder {

	public static void main(String[] args) {
		String basePackage = "com.itranswarp.crypto";
		WarpDb db = new WarpDb();
		db.setBasePackages(Arrays.asList(basePackage));
		db.init();
		System.out.println("\n\n-- Generated DDL --\n\n");
		System.out.println("DROP DATABASE IF EXISTS exchange;\n\n");
		System.out.println("CREATE DATABASE exchange;\n\n");
		System.out.println("USE exchange;\n\n");
		System.out.println(db.exportSchema());
	}
}
