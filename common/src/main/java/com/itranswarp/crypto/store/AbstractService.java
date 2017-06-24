package com.itranswarp.crypto.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.itranswarp.warpdb.WarpDb;

/**
 * Base class for service interface.
 * 
 * @author liaoxuefeng
 */
public abstract class AbstractService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected WarpDb db;
}
