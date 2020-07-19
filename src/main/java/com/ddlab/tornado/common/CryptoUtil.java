/*
 * Copyright 2020 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.tornado.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * The Class CryptoUtil.
 *
 * @author Debadatta Mishra
 */
public class CryptoUtil {

	/** The secret key. */
	private static SecretKeySpec secretKey;

	/** The key. */
	private static byte[] key;

	/** The cipher algorithm. */
	private static String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";

	/** The charset. */
	private static String CHARSET = "UTF-8";

	/** The sha 1. */
	private static String SHA_1 = "SHA-1";

	/** The key algo. */
	private static String KEY_ALGO = "AES";

	static {
		initKey("HatiGhodaBagha");
	}

	/**
	 * Inits the key.
	 *
	 * @param myKey the my key
	 */
	public static void initKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes(CHARSET);
			sha = MessageDigest.getInstance(SHA_1);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, KEY_ALGO);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the encrypted value.
	 *
	 * @param strToEncrypt the str to encrypt
	 * @return the encrypted value
	 */
	public static String getEncryptedValue(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] cipherBytes = cipher.doFinal(strToEncrypt.getBytes(CHARSET));
			return Base64.getEncoder().encodeToString(cipherBytes);
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * Gets the decrypted value.
	 *
	 * @param strToDecrypt the str to decrypt
	 * @return the decrypted value
	 */
	public static String getDecryptedValue(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
}
