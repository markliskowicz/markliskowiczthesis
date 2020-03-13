package com.example.demo;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
	private static final String key = "1213";
	
	public static byte[] encrypt(byte[] contents) {
		byte[] result;
		byte[] keyData = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Bowfish");
		try {
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		result = cipher.doFinal(contents);
		result = Base64.getEncoder().encode(result);
		} catch(Exception e) {
			return contents;
		}
		return result;
	}
	
	public static byte[] decrypt(byte[] contents) {
		byte[] result;
		byte[] keyData = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Bowfish");
		try {
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		result = cipher.doFinal(Base64.getDecoder().decode(contents));
		} catch(Exception e) {
			return contents;
		}
		return result;
	}
}