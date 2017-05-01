package org.ql.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ql.utils.debug.QLLog;

import android.text.TextUtils;

public class QLStringUtils {

	/**
	 * 视频时间转换 
	 * @param t 毫秒
	 */
	public static String millisecondToDate(long t) {
		long i = t;
		i /= 1000;
		long minute = i / 60;
		long hour = minute / 60;
		long second = i % 60;
		minute %= 60;
		if (hour <= 0)
			return String.format("%02d:%02d", minute, second);
		else
			return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	
	/**
	 * 格式化成时间格式比如120s --> 02:00
	 * @param milliseconds
	 * @return
	 */
	public static String generateTime(long milliseconds){
		StringBuilder sb = new StringBuilder();
		long h,m,s;
		h = milliseconds/(3600000);
		if(h>0){
			sb.append(h+":");
		}
		m = (milliseconds-h*3600000)/60000;
		if(m<10){
			sb.append("0"+m+":");
		}else{
			sb.append(m+":");
		}
		s = milliseconds % 60000/1000;
		if(s<10){
			sb.append("0"+s);
		}else{
			sb.append(""+s);
		}
		return sb.toString();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean isEmail(String email) {
		if ((email == null) || (email.trim().equals(""))) {
			return false;
		}
		String emailRegular = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(emailRegular);
		Matcher matcher = regex.matcher(email);
		boolean isMatched = matcher.matches();
		return isMatched;
	}
	
	/** 判断电话号码 **/
	public static boolean isPhoneNumberValid(String phoneNumber) {
		if ((phoneNumber == null) || (phoneNumber.trim().equals(""))) {
			return false;
		}
		boolean isValid = false;
		String expression_r_r = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		Pattern pattern = Pattern.compile(expression_r_r);
		Matcher matcher = pattern.matcher(phoneNumber);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
	
	/**
	 * 判断IP是否在指定范围；
	 */
	public static boolean ipIsValid(String ipSection, String ip) {
		if (TextUtils.isEmpty(ipSection)){
			QLLog.e("IP段不能为空！");
			return false;
		}
		if (TextUtils.isEmpty(ip)){
			QLLog.e("IP不能为空！");
			return false;
		}
		
		ipSection = ipSection.trim();
		ip = ip.trim();
		final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
		final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
		if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
			return false;
		int idx = ipSection.indexOf('-');
		String[] sips = ipSection.substring(0, idx).split("\\.");
		String[] sipe = ipSection.substring(idx + 1).split("\\.");
		String[] sipt = ip.split("\\.");
		long ips = 0L, ipe = 0L, ipt = 0L;
		for (int i = 0; i < 4; ++i) {
			ips = ips << 8 | Integer.parseInt(sips[i]);
			ipe = ipe << 8 | Integer.parseInt(sipe[i]);
			ipt = ipt << 8 | Integer.parseInt(sipt[i]);
		}
		if (ips > ipe) {
			long t = ips;
			ips = ipe;
			ipe = t;
		}
		return ips <= ipt && ipt <= ipe;
	}

	/**
	 * 半角转全角
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	
	/**
	 * 字符串转string
	 * @param arrayOfString
	 * @return
	 */
	public static List<String> stringsToList(String[] arrayOfString) {
		if ((arrayOfString == null) || (arrayOfString.length == 0))
			return null;
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < arrayOfString.length; i++)
			list.add(arrayOfString[i]);
		return list;
	}
	
	
	/**
	 * 往url里面添加参数
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 */
	public static String addParamToUrl(String url,String key,Object value){
		if(TextUtils.isEmpty(url)){
			return null;
		}
		String tmpKey = key+"=";
		if(url.indexOf(tmpKey)!=-1){
			String[] tmp = url.split(tmpKey);
			if(tmp.length>1){
				if(tmp[1].indexOf("&")!=-1){
					String subStr = tmp[1].substring(tmp[1].indexOf("&"), tmp[1].length());
					url = tmp[0] + tmpKey + value + subStr;
				}else{
					url = tmp[0] + tmpKey + value;
				}
			}
		}else{			
			if(url.indexOf("?")==-1){
				url += "?"+key+"="+value;
			}else{
				url += "&"+key+"="+value;
			}
		}
		return url;
	}
}
