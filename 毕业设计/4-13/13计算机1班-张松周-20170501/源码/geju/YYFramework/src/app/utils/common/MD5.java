package app.utils.common;

import java.security.NoSuchAlgorithmException;

public class MD5 {

	/**
	 * MD5加密
	 * @param source
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String encode(byte[] source){
		try {
			char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
					'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			// MD5 的计算结果是一个 128 位的长整数，
			byte tmp[] = md.digest(); 
			// 用字节表示就是 16 个字节
			// 每个字节用 16 进制表示的话，使用两个字符，
			char str[] = new char[16 * 2]; 
			// 所以表示成 16 进制需要 32 个字符
			// 表示转换结果中对应的字符位置
			int k = 0; 
			for (int i = 0; i < 16; i++) { 
				// 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				// 取第 i 个字节
				byte byte0 = tmp[i]; 
				// 取字节中高 4 位的数字转换,
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; 
				// >>> 为逻辑右移，将符号位一起右移
				// 取字节中低 4 位的数字转换
				str[k++] = hexDigits[byte0 & 0xf];
			}
			String s = new String(str);
			md = null;
			// 换后的结果转换为字符串
			return s!=null?s:null; 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
