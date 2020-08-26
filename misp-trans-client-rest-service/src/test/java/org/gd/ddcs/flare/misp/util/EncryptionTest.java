package org.gd.ddcs.flare.misp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EncryptionTest {
	@Test
	public void test() {
		String password = "password";
		String secret = "secret";

		String encrypted = EncryptionUtil.encrypt(password, secret);
		System.out.println("encrypted: " + encrypted);
		assertEquals(password, EncryptionUtil.decrypt(encrypted, secret));
	}
}
