package bg.sofia.uni.fmi.mjt.authentication.connection;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class PasswordCryptor { // encrypting algorithm from google

	private PasswordCryptor() {
	}

	private static final String HEX = "0123456789ABCDEF";

	private static final String SEED = "bg.sofia.uni.fmi.mjt.authenticationserver";

	private static final int FOUR_INDEX = 4;
	private static final int INDEX_128 = 128;
	private static final int CONSTANT_HEX = 0x0f;

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(PasswordCryptor.HEX.charAt((b >> FOUR_INDEX) & CONSTANT_HEX))
			.append(PasswordCryptor.HEX.charAt(b & CONSTANT_HEX));
	}

	private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(INDEX_128, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String encryptToHex(String cleartext) throws Exception {
		byte[] rawKey = PasswordCryptor.getRawKey(PasswordCryptor.SEED.getBytes());
		byte[] result = PasswordCryptor.encrypt(rawKey, cleartext.getBytes());
		return PasswordCryptor.toHex(result);
	}

	public static String decrypt(String encrypted) throws Exception {
		byte[] rawKey = PasswordCryptor.getRawKey(PasswordCryptor.SEED.getBytes());
		byte[] enc = PasswordCryptor.toByte(encrypted);
		byte[] result = PasswordCryptor.decrypt(rawKey, enc);
		return new String(result);
	}

	public static String toHex(String txt) {
		return PasswordCryptor.toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(PasswordCryptor.toByte(hex));
	}

	final static int INDEX_16 = 16;

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), INDEX_16).byteValue();
		}
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null) {
			return "";
		}
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			PasswordCryptor.appendHex(result, buf[i]);
		}
		return result.toString();
	}

}