package com.weblegit.urlshortner;

import java.security.SecureRandom;
import java.util.Random;

public class Util {

	private static final char[] DEFAULT_CODEC = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
			.toCharArray();

	private static Random random = new SecureRandom();

	public static String generate() {
		byte[] verifierBytes = new byte[6];
		random.nextBytes(verifierBytes);

		char[] chars = new char[verifierBytes.length];
		for (int i = 0; i < verifierBytes.length; i++) {
			chars[i] = DEFAULT_CODEC[((verifierBytes[i] & 0xFF) % DEFAULT_CODEC.length)];
		}
		return new String(chars);

	}

}
