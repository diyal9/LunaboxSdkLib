package com.lunabox.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * 封装DES加解密相关接口
 * 
 * @author laian.wang
 */
public class DesUtil {
	private static char[] hexArr;

	static {
		char[] arrayOfChar = new char[16];
		arrayOfChar[0] = 48;
		arrayOfChar[1] = 49;
		arrayOfChar[2] = 50;
		arrayOfChar[3] = 51;
		arrayOfChar[4] = 52;
		arrayOfChar[5] = 53;
		arrayOfChar[6] = 54;
		arrayOfChar[7] = 55;
		arrayOfChar[8] = 56;
		arrayOfChar[9] = 57;
		arrayOfChar[10] = 97;
		arrayOfChar[11] = 98;
		arrayOfChar[12] = 99;
		arrayOfChar[13] = 100;
		arrayOfChar[14] = 101;
		arrayOfChar[15] = 102;
		hexArr = arrayOfChar;
	}

	public static String randHexStr(int len) {
		LogWriter.print("DesUtil randHexStr len:" + len);

		StringBuilder localStringBuilder = new StringBuilder();
		Random localRandom = new Random(System.currentTimeMillis());
		for (int i = 0; i < len; i++) {
			localStringBuilder
					.append(hexArr[localRandom.nextInt(hexArr.length)]);
		}

		String str = localStringBuilder.toString();
		LogWriter.print("DesUtil randHexStr ret:" + str);
		return str;
	}

	public static String genDesKey() {
		byte[] keyArr = null;
		StringBuilder sb = new StringBuilder(24);

		try {
			for (int i = 0; i < 12; i++) {
				keyArr = KeyGenerator.getInstance(new String("DESede"))
						.generateKey().getEncoded();

				sb.append(hexArr[((0xF0 & keyArr[i]) >>> 4)]);
				sb.append(hexArr[(0xF & keyArr[i])]);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		LogWriter.print("DesUtil genDesKey ret:" + sb.toString());
		return sb.toString();
	}

	private static byte[] a(byte[] paramArrayOfByte) {
		if (paramArrayOfByte.length >= 24) {
			return paramArrayOfByte;
		} else {
			int i = paramArrayOfByte.length;
			byte[] arrayOfByte1 = null;
			byte[] arrayOfByte2 = null;
			if (paramArrayOfByte.length < 24) {
				arrayOfByte1 = new byte[24];
				arrayOfByte2 = new byte[24 - i];
			}
			for (int j = 0;; j++) {
				if (j >= 24 - i) {
					System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, i);
					System.arraycopy(arrayOfByte2, 0, arrayOfByte1, i, 24 - i);
					paramArrayOfByte = arrayOfByte1;
					return paramArrayOfByte;
				}
				arrayOfByte2[j] = 0;
			}
		}
	}

	private static IvParameterSpec b(byte[] paramArrayOfByte) {
		byte[] arrayOfByte = new byte[8];
		System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 8);
		return new IvParameterSpec(arrayOfByte);
	}

	public static byte[] encrypt(byte[] res, byte[] key)
			throws NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException,
			InvalidKeyException, InvalidKeySpecException,
			NoSuchAlgorithmException {
		LogWriter.print("DesUtil encrypt() 1 bytes1Len:"
				+ res.length + " bytes2Len:" + key.length);
		LogWriter.print("DesUtil encrypt() 2 byte1Str:"
				+ new String(res) + " byte2Str:" + new String(key));
		Cipher localCipher = Cipher.getInstance(new String(
				"DESede/CBC/PKCS7Padding"));
		localCipher.init(1, SecretKeyFactory.getInstance("DESede")
				.generateSecret(new DESedeKeySpec(a(key))), b(key));
		byte[] arrayOfByte = localCipher.doFinal(res);
		LogWriter.print("DesUtil encrypt() 3 retBytesLen:"
				+ arrayOfByte.length);

		return arrayOfByte;

	}

	public static byte[] decrypt(byte[] paramArrayOfByte1,
			byte[] paramArrayOfByte2) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidKeySpecException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		LogWriter.print("DesUtil decrypt decrypt() 3 bytes1Len:"
				+ paramArrayOfByte1.length + " bytes2Len:"
				+ paramArrayOfByte2.length);
		Cipher localCipher = Cipher.getInstance("DESede/CBC/PKCS7Padding");
		DESedeKeySpec localDESedeKeySpec = new DESedeKeySpec(paramArrayOfByte2);
		localCipher.init(2, SecretKeyFactory.getInstance("DESede")
				.generateSecret(localDESedeKeySpec), b(paramArrayOfByte2));
		byte[] arrayOfByte = localCipher.doFinal(paramArrayOfByte1);
		LogWriter.print("DesUtil decrypt decrypt() retLen:"
				+ arrayOfByte.length + " retStr:" + new String(arrayOfByte));
		return arrayOfByte;
	}
}
