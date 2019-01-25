package tools;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encoder {

	private static final String KEY = "fs4xSgs3shdk";
	private static final String CIPHER_TYPE = "Blowfish";
	
	public static void main(String[] args) {
		double averageTime = 0;
		int count = 100;
		for(int i=0; i < count; i++) {
			Stopwatch s = new Stopwatch();
			s.start();
			System.out.println(decode(encode("Halli hallo")));
			s.stop();
			averageTime += s.getDurationInMillis();
		}
		averageTime /= count;
		System.out.println(averageTime);
	}
	
	public static String encode(String s) {
		return encodeBase64(s);
	}
	
	public static String decode(String s) {
		return decodeBase64(s);
	}
	
	public static String encodeBase64(String s) {
		return new String(Base64.getEncoder().encode(s.getBytes()));
	}
	
	public static String decodeBase64(String s) {
		return new String(Base64.getDecoder().decode(s.getBytes()));
	}
	
	public static String encodeCipher(String string) {
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), CIPHER_TYPE);
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] encrypted = cipher.doFinal(string.getBytes());
			return new String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decodeCipher(String strEncrypted) {
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), CIPHER_TYPE);
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
			return new String(decrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
