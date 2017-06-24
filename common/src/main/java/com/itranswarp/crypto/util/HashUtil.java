package com.itranswarp.crypto.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing.
 * 
 * @author liaoxuefeng
 */
public class HashUtil {

	/**
	 * Generate SHA-1 as hex string (all lower-case).
	 * 
	 * @param input
	 *            Input as bytes.
	 * @return Hex string.
	 */
	public static String sha1(byte[] input) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.update(input);
		byte[] digest = md.digest();
		return toHexString(digest);
	}

	/**
	 * Convert bytes to hex string (all lower-case).
	 * 
	 * @param b
	 *            Input bytes.
	 * @return Hex string.
	 */
	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length << 2);
		for (byte x : b) {
			int hi = (x & 0xf0) >> 4;
			int lo = x & 0x0f;
			sb.append(HEX_CHARS[hi]);
			sb.append(HEX_CHARS[lo]);
		}
		return sb.toString().trim();
	}

	static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

}
