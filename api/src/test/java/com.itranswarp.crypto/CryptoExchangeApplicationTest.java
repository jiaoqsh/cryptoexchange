package com.itranswarp.crypto;

import com.itranswarp.warpdb.WarpDb;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by jiaoqsh on 18/2/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoExchangeApplication.class)
public class CryptoExchangeApplicationTest {

    @Autowired
    private WarpDb warpDb;

    @Test
    public void exportSchema() throws Exception {
        String schema = warpDb.exportSchema();
        System.out.println(schema);
    }
}