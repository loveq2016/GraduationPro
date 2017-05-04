package org.ql.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.R.integer;

/**
 * 类名称：QLDateUtils <br>
 * 类描述：日期工具集合 <br>
 * 创建者：anan <br>
 * 创建时间：2013-3-1 下午2:00:33 <br>
 * 修改者：anan <br>
 * 修改时间：2013-3-1 下午2:00:33 <br>
 * 修改备注： <br>
 * 
 * @version
 */
public class QLDateUtils {

	/**
	 * 判断是否是当天
	 * 
	 * @param dt
	 * @return
	 */
	public static boolean isToday(Date dt) {
		Date today = new Date(System.currentTimeMillis());
		int year1 = today.getYear();
		int year2 = dt.getYear();
		int month1 = today.getMonth();
		int month2 = dt.getMonth();
		int day1 = today.getDate();
		int day2 = dt.getDate();
		if (year1 == year2 && month1 == month2 && day1 == day2) {
			return true;
		} else {
			return false;
		}
	}

	private static final String[] easyReadingStringList = { "前天", "昨天", "今天",
			"明天", "后天", "年", "月", "天" };

	/**
	 * 获取
	 * 
	 * @param dt
	 * @return
	 */
	public static String toEasyReadingString(Date dt) {
		String str = null;
		Date dtNow = getDateTimeNow();
		long dtNowTime = dtNow.getTime();
		long dtTargetTime = dt.getTime();
		long offset = dtTargetTime - dtNowTime;
		long offset_day = offset / (60 * 60 * 24);
		int leng = Math.abs((int) offset_day);
		if (leng > 2) {
			// xx天前/后
			long years = offset / (60 * 60 * 24 * 360);
			long months = offset / (60 * 60 * 24 * 30);
			int idx = 5;
			StringBuilder builder = new StringBuilder();
			if (years > 0) {
				idx++;
				builder.append(years);
			} else if (months > 0) {
				idx += 2;
				builder.append(months);
			} else {
				idx += 3;
				builder.append(leng);
			}
			builder.append(easyReadingStringList[idx]);
			builder.append(offset_day < 0 ? "前" : "后");
			str = builder.toString();
		} else {
			int idx = (int) offset_day + 3;
			str = easyReadingStringList[idx];
		}
		return str;
	}

	/**
	 * 是否为同一天
	 * 
	 * @param dt1
	 * @param dt2
	 * @return
	 */
	public static boolean isSameDay(Date dt1, Date dt2) {
		if (dt1 == null || dt2 == null) {
			return false;
		}
		String dt1_str = QLDateUtils.getTimeWithFormat(dt1, "yyyyMMdd");
		String dt2_str = QLDateUtils.getTimeWithFormat(dt2, "yyyyMMdd");
		return dt1_str.equals(dt2_str);
	}

	private static String[] weekdata_list = { "日", "一", "二", "三", "四", "五", "六" };

	/**
	 * 获取星期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekDateString(Date date) {
		if (date == null) {
			date = new Date(getCurrMillis());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		week = week < 0 ? 0 : week;
		String weekdate = weekdata_list[week];
		return ("星期" + weekdate);
	}

	/**
	 * 获取当月的第一天
	 * 
	 * @param date
	 * @param dt_format
	 * @return
	 */
	public static String getMonthStartDay(Date date, String dt_format) {
		if (date == null || dt_format == null) {
			return null;
		}
		Date startTime = getFirstDayOfMonth(date);
		return getTimeWithFormat(startTime, dt_format);
	}

	public static Date getFirstDayOfMonth(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static Date getTheLastDayOfMonth(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1); // 加一个月
		calendar.set(Calendar.DAY_OF_MONTH, 1); // 设置为该月第一天
		calendar.add(Calendar.DATE, -1); // 再减一天即为最后一天
		return calendar.getTime();
	}

	/**
	 * 获取当月的最后一天
	 * 
	 * @param date
	 * @param dt_format
	 * @return
	 */
	public static String getMonthEndDay(Date date, String dt_format) {
		if (date == null || dt_format == null) {
			return null;
		}
		Date endTime = getTheLastDayOfMonth(date);
		return getTimeWithFormat(endTime, dt_format);
	}

	/**
	 * 从字符串创建日期时间
	 * 
	 * @param dt_str
	 * @param dt_format
	 * @return
	 */
	public static Date createDateTimeFromString(String dt_str, String dt_format) {
		if (dt_str == null || dt_format == null) {
			return null;
		}
		Date dt = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(dt_format);
			dt = formatter.parse(dt_str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt;
	}

	/**
	 * 获取当前毫秒数
	 * 
	 * @return
	 */
	public static long getCurrMillis() {
		long _t = 0L;
		try {
			_t = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
			return _t;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		_t = new Date().getTime();
		return _t;
	}

	/**
	 * 获取格式化时间日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getTimeWithFormat(Date date, String format) {
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		if (date == null) {
			date = getDateTimeNow();
		}
		return sdf.format(date);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static Date getDateTimeNow() {
		return new Date(getCurrMillis());
	}

}
