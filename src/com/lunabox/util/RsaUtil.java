package com.lunabox.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.content.Context;


public class RsaUtil {
	public static byte[] encrpty(byte[] content, String publicKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		LogWriter.print("RsaUtil encrpty() bytesCnt:"
				+ content.length + " key1:" + publicKey);
		LogWriter.print("RsaUtil a() before RSA str:"
				+ new String(content));
		BigInteger localBigInteger = new BigInteger("010001", 16);
		RSAPublicKeySpec localRSAPublicKeySpec = new RSAPublicKeySpec(
				new BigInteger(publicKey, 16), localBigInteger);
		PublicKey localPublicKey = KeyFactory.getInstance("RSA")
				.generatePublic(localRSAPublicKeySpec);
		Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		localCipher.init(1, localPublicKey);
		byte[] arrayOfByte = localCipher.doFinal(content);
		LogWriter.print("RsaUtil encrpty() after RSA bytesCnt:"
				+ arrayOfByte.length);
		return arrayOfByte;
	}
}
