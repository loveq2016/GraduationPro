package app.utils.common;

import java.io.IOException;
import java.security.MessageDigest;

import android.text.TextUtils;

/**
 * @author Avan - 2012-03-28
 */
public class EncryptUtils {

	/**
	 * 鐩愬�
	 */
	private static final String SALT_FIGURE = "pms";

	/**
	 * BASE65鍔犲瘑
	 * @param value
	 * @return String
	 */
	public static String getBase64Encrypt(String value) {
		String val = Base64.encodeBytes(value.getBytes());
		return val;
	}

	/**
	 * BASE65瑙ｅ瘑
	 * 
	 * @param value
	 * @return String
	 */
	public static String getBase64Decrypt(String value) {
		String val = null;
		try {
			byte[] bytes = Base64.decode(value);
			val = new String(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * MD5鎽樿鍔犲瘑
	 * 
	 * @param value
	 * @return String
	 */
	public static String getMD5(String value) {
		String val = MD5.encode(value.getBytes());
		return val;
	}

	/**
	 * BASE64MD5鎽樿鍔犲瘑
	 * @param value
	 * @return String
	 */
	public static String getBase64MD5(String value) {
		if(TextUtils.isEmpty(value))
			return "";
		String val = "";
		val = Base64.encodeBytes(MD5.encode(value.getBytes()).getBytes());
		
		/*try{
			MessageDigest md5=MessageDigest.getInstance("MD5");
			val = Base64.encodeBytes(md5.digest(value.getBytes("utf-8")));
		}catch (Exception e) {}*/
		
		return val;
	}

	/**
	 * 澧炲姞鐩愬�
	 * 
	 * @param value
	 * @return String
	 */
	public static String mergeSalt(String value) {
		return value + "{" + SALT_FIGURE + "}";
	}

	/**
	 * 鍒犻櫎鐩愬�
	 * 
	 * @param value
	 * @return String
	 */
	public static String removeSalt(String value) {
		return value.replace("{" + SALT_FIGURE + "}", "");
	}
}
