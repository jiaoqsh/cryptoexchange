package com.itranswarp.crypto.match;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Hold object status by hash.
 * 
 * @author liaoxuefeng
 */
public class HashStatus {

	final MessageDigest md;
	byte[] status;

	public HashStatus() {
		try {
			this.md = MessageDigest.getInstance("MD5");
			this.status = md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateStatus(byte[] data) {
		this.md.update(data);
		this.status = this.md.digest();
	}

	public void updateStatus(String data) {
		updateStatus(data.getBytes(StandardCharsets.UTF_8));
	}

	public void updateStatus(ByteBuffer data) {
		this.md.update(data);
		this.status = this.md.digest();
	}

	public byte[] getStatus() {
		return Arrays.copyOf(this.status, this.status.length);
	}
}
