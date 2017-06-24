package com.itranswarp.crypto.store;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@Commit
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:/test.properties")
@ContextConfiguration(classes = { DbTestConfiguration.class })
public class DbTest extends DbTestBase {

	@Before
	public void setUp() throws Exception {
		createTable(UserEntity.class);
		createTable(AutoIncrementEntity.class);
		createTable(UserEntity.class);
		createTable(AutoIncrementEntity.class);
	}

	@Test
	public void testInsert() {
		this.db.save(newUser(1, "Bob"));
	}

	@Test
	public void testInsertAutoIncrement() {
		AutoIncrementEntity ai1 = new AutoIncrementEntity();
		ai1.createdAt = ai1.updatedAt = System.currentTimeMillis();
		AutoIncrementEntity ai2 = new AutoIncrementEntity();
		ai2.createdAt = ai2.updatedAt = System.currentTimeMillis();
		this.db.save(ai1);
		this.db.save(ai2);
		assertEquals(1, ai1.id);
		assertEquals(2, ai2.id);
	}

	@Test
	public void testBatchInsert() {
		this.db.save(newUser(1, "Bob"), newUser(2, "Alice"));
	}

	@Test
	public void testBatchInsertAutoIncrement() {
		AutoIncrementEntity ai1 = new AutoIncrementEntity();
		ai1.createdAt = ai1.updatedAt = System.currentTimeMillis();
		AutoIncrementEntity ai2 = new AutoIncrementEntity();
		ai2.createdAt = ai2.updatedAt = System.currentTimeMillis();
		this.db.save(ai1, ai2);
		assertEquals(1, ai1.id);
		assertEquals(2, ai2.id);
	}

	@Test
	public void testUpdate() {
		UserEntity user = newUser(1, "Bob");
		this.db.save(user);
		user.type = "vip";
		user.name = "Alice";
		this.db.update(user);
	}

	@Test
	public void testDelete() {
		UserEntity user = newUser(1, "Bob");
		this.db.save(user);
		this.db.remove(user);
	}

	UserEntity newUser(long id, String name) {
		UserEntity user = new UserEntity();
		user.id = id;
		user.type = "normal";
		user.name = name;
		user.createdAt = user.updatedAt = System.currentTimeMillis();
		user.version = 0;
		return user;
	}
}
