package app.utils.common;

import java.io.File;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.ql.utils.QLToastUtils;
import org.ql.utils.debug.QLLog;
import org.ql.utils.gson.QLTimestampTypeAdapter;
import org.ql.utils.storage.QLSp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 
 * @author SiuJiYung
 * create at 2013-12-3 下午1:55:22
 *</br>
 *
 *公共工具类
 */
public class Public {

private static QLSp sp;
	
	public static boolean clearBuffer = true;
	/**
	 * 页面自动刷新间隔
	 */
	public static final int AUTOREFRESH_TIME = 3*1000;
	
	private static Gson gson;
		
	/** 服务器返回的时间格式 */
	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	/** 中文时间格式 */
	public static SimpleDateFormat formatterCH = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
	
	/** 格式化日期，只要日期 */
	public static SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	/** 格式化日期，只要时间 */
	public static SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	public static SimpleDateFormat formatterCHDate = new SimpleDateFormat("MM月dd日HH时mm分", Locale.getDefault());
	
	/**mini播放器的屏显模式*/
	public static final String KEY_SP_MINIPLAYER_LAYOUT = "miniPlayerLayout";
	/**播放器的屏显模式*/
	public static final String KEY_SP_PLAYER_LAYOUT = "playerLayout";
	
	private static long timeOffset = 0;
	
//	public static boolean v1_uploadInterfaceEnable = false;
	
	/**
	 * 获取一个QLSp对象，使用沙盒的xml文件进行持久化存储
	 * @param context
	 * @return
	 */
	public static QLSp getSp(Context context){
		if(null==sp){
			sp = new QLSp(context);
		}
		return sp;
	}
	
	
	/**
	 * 获取GSON
	 * @param activity
	 * @return
	 */
	public static Gson getGson(){
		if(null==gson){
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");  
			gsonBuilder.registerTypeAdapter(Timestamp.class,new QLTimestampTypeAdapter()); 
			gson = gsonBuilder.create();
		}
		return gson;
	}
	
	public static Listener<Long,Object> synTimeListener = new Listener<Long, Object>() {
		@Override
		public void onCallBack(Long status, Object reply) {
			timeOffset = status;
		}
	};
	
	/**
	 * 获取是星期几
	 * @param date
	 * @return
	 */
	public static int getWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week < 0) {
			return week = 0;
		} else {
			return week;
		}
	}
	/**
	 * 判断两日期是否为同一天
	 * @param dt
	 * @param dt2
	 * @return
	 */
	public static boolean isTheSameDay(Date dt,Date dt2){
		Calendar calDateA = Calendar.getInstance();
	    calDateA.setTime(dt);
	    Calendar calDateB = Calendar.getInstance();
	    calDateB.setTime(dt2);
	    return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
	            && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
	            &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
	}
	/**
	 * 获取当天0晨0点时间的毫秒数
	 * @param dt
	 * @return
	 */
	public static long createThisDayZearTime(Date dt){
		if (dt == null) {
			dt = Public.getDateTimeNow();
		}
		Calendar calDate = Calendar.getInstance();
	    calDate.setTime(dt);
	    Calendar calDateZearoTime = Calendar.getInstance();
	    calDateZearoTime.set(
	    		calDate.get(Calendar.YEAR), 
	    		calDate.get(Calendar.MONTH), 
	    		calDate.get(Calendar.DAY_OF_MONTH), 
	    		0, 0,0);
	    return calDateZearoTime.getTimeInMillis();
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getTimeNow(){
		long currMillis = getCurrMillis();
		Date dt = new Date(currMillis);
		return Public.formatter.format(dt);
	}
	
	public static long getTimeMillsWithDateTime(String dtstr){
		long mills = -1;
		if (dtstr == null) {
			return mills;
		}
		try {
			Date dtDate = Public.formatter.parse(dtstr);
			Calendar calDateZearoTime = Calendar.getInstance();
			calDateZearoTime.setTime(dtDate);
			mills = calDateZearoTime.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mills;
	}
	
	public static long getCurrMillis(){
		long _t = 0L;
		try {
			_t = Calendar.getInstance(Locale.getDefault()).getTimeInMillis() + timeOffset;
			return _t;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		_t = new Date().getTime();
		return _t;
	}
	
	public static String getTimeWithFormat(String format){
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		return sdf.format(getDateTimeNow());
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static Date getDateTimeNow(){
		return new Date(getCurrMillis());
	}
	
	public static boolean isSdCardExist(Context context,boolean showToast){
		boolean sdCardExist = Environment.getExternalStorageState() .equals(android.os.Environment.MEDIA_MOUNTED); 
		if(!sdCardExist && showToast){
			QLToastUtils.showToast(context,"没有SD卡，请插入SD后再进行操作");
		}else if(!sdCardExist && !showToast){
			QLLog.pln("没有SD卡，请插入SD后再进行操作");
		}
		return sdCardExist;
	}
	public static int  getWindowWid(Activity activit){
		WindowManager manager = activit.getWindowManager();
		int height = manager.getDefaultDisplay().getHeight();
		  return  manager.getDefaultDisplay().getWidth();

	}
	public static long SdCardSpace(Context context){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			return availCount*blockSize/1024;
//			Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
//			Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
		}
		return 0;
	}
	
	/**
	 * @return 程序版本
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "1.0";
			}
		} catch (Exception e) {
			return "1.0";
		}
		return versionName;
	}
	
	
	/**
	 * @return 程序版本号
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
		}
		return versionCode;
	}
	
	/**
	 * 处理栏目查询中的关键字，分隔符替换成"|"
	 * @param keyWords
	 * @return
	 */
	public static String dealKeyWords(String keyWords){
		if(!TextUtils.isEmpty(keyWords)){
			keyWords = keyWords.replaceAll(",", "|");
			keyWords = keyWords.replaceAll("，", "|");
			keyWords = keyWords.replaceAll(" ", "|");
		}
		return keyWords;
	}
	
	/**
	 * 获取sdcard路径
	 * @return
	 */
	public static String getAppSdcardPath(){
		File file = new File(Environment.getExternalStorageDirectory(),"android/data/"+AndroidFactory.getApplicationContext().getPackageName());
		if(!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	
	private static SimpleDateFormat formatNotSS = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	
//	public static DisplayImageOptions getDisplayImageOptions(){
//		return getDisplayImageOptions(0);
//	}
//	
//	public static DisplayImageOptions getDisplayImageOptions(int image){
//		return getDisplayImageOptions(image,image,image);
//	}
//	
//	public static DisplayImageOptions getDisplayImageOptions(int imageOnLoading,int imageForEmptyUri,int imageOnFail){
//		int defImage = R.drawable.nodata_img;
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(imageOnLoading > 0 ? imageOnLoading : defImage)
//		.showImageForEmptyUri(imageForEmptyUri > 0 ? imageForEmptyUri : defImage)
//		.showImageOnFail(imageOnFail > 0 ? imageOnFail : defImage)
//		.build();
//		return options;
//	}

}
