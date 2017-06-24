package com.itranswarp.crypto.match;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class HashStatusTest {
	HashStatus hs1;
	HashStatus hs2;

	@Before
	public void setUp() {
		hs1 = new HashStatus();
		hs2 = new HashStatus();
	}

	@Test
	public void testUpdateStatusByteArray() {
		Random random = new Random(12345L);
		for (int i = 0; i < 100; i++) {
			byte[] data = new byte[20 + i];
			random.nextBytes(data);
			hs1.updateStatus(data);
			hs2.updateStatus(data);
			assertArrayEquals(hs1.getStatus(), hs2.getStatus());
		}
	}

	@Test
	public void testUpdateStatusString() {
		for (int i = 0; i < 100; i++) {
			String s = "Hello, \u4f60\u597d, " + i;
			hs1.updateStatus(s);
			hs2.updateStatus(s);
			assertArrayEquals(hs1.getStatus(), hs2.getStatus());
		}
	}

	@Test
	public void testUpdateStatusByteBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		Random random = new Random(12345L);
		for (int i = 0; i < 100; i++) {
			fillBuffer(buffer, random);
			hs1.updateStatus(buffer);
			fillBuffer(buffer, random);
			hs2.updateStatus(buffer);
			assertArrayEquals(hs1.getStatus(), hs2.getStatus());
		}
	}

	@Test
	public void testUpdateStatusAll() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		Random random = new Random(12345L);
		for (int i = 0; i < 100; i++) {
			// update by bytes:
			byte[] data = new byte[20 + i];
			random.nextBytes(data);
			hs1.updateStatus(data);
			hs2.updateStatus(data);
			assertArrayEquals(hs1.getStatus(), hs2.getStatus());
			// update by String:
			String s = "Hello, \u4f60\u597d, " + i;
			hs1.updateStatus(s);
			hs2.updateStatus(s);
			assertArrayEquals(hs1.getStatus(), hs2.getStatus());
			// update by ByteBuffer:
			fillBuffer(buffer, random);
			hs1.updateStatus(buffer);
			fillBuffer(buffer, random);
			hs2.updateStatus(buffer);
			assertArrayEquals(hs1.getStatus(), hs2.getStatus());
		}
	}

	void fillBuffer(ByteBuffer buffer, Random random) {
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		long l = random.nextLong();
		int n = random.nextInt();
		double d = random.nextDouble();
		float f = random.nextFloat();
		buffer.clear();
		buffer.put(bytes);
		buffer.putLong(l);
		buffer.putInt(n);
		buffer.putDouble(d);
		buffer.putFloat(f);
	}
}
