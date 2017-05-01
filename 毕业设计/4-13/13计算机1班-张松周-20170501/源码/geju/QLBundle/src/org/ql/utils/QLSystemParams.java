package org.ql.utils;

import java.util.Locale;

import org.ql.utils.debug.QLLog;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取系统相关参数
 */
public class QLSystemParams {
		private final String tag = QLSystemParams.class.getSimpleName();
	   
	    
	    public String deviceType = "android";
	    public String deviceName = "";//手机型号
	    public String deviceOSVersion = "";// 操作系统版本.
	    public String deviceCountryCode = "";
	    public String deviceLanguage = "";
	    public String imei = "";
	    public String imsi = "";
	    public String simOperator = "";
	    
	    public int deviceScreenWidth = 0;
	    public int deviceScreenHeight = 0;
	   
	    public String appVersion = "";
	    public String appVersionCode = "";
	    
	   
	    public void log(){
	    	QLLog.i(tag, "============start============");
	    	QLLog.v(tag, "deviceType="+deviceType);
	    	QLLog.v(tag, "deviceName="+deviceName);
	    	QLLog.v(tag, "deviceOSVersion="+deviceOSVersion);
	    	QLLog.v(tag, "deviceCountryCode="+deviceCountryCode);
	    	QLLog.v(tag, "deviceLanguage="+deviceLanguage);
	    	QLLog.v(tag, "imei="+imei);
	    	QLLog.v(tag, "imsi="+imsi);
	    	QLLog.v(tag, "simOperator="+simOperator);
	    	QLLog.v(tag, "deviceScreenWidth="+deviceScreenWidth);
	    	QLLog.v(tag, "deviceScreenHeight="+deviceScreenHeight);
	    	QLLog.v(tag, "appVersion="+appVersion);
	    	QLLog.v(tag, "appVersionCode="+appVersionCode);
	    	QLLog.i(tag, "============end============");
	    }
	    private static QLSystemParams appParams;
	    private QLSystemParams(Context context){
	    	
	    	PackageManager manager = context.getPackageManager();
	    	TelephonyManager t = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
	    	
	    	deviceName = android.os.Build.MODEL;
	    	deviceOSVersion = android.os.Build.VERSION.RELEASE;
	    	// 国家代码.
            deviceCountryCode = Locale.getDefault().getCountry();
            // 语言代码.
            deviceLanguage = Locale.getDefault().getLanguage();
            
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            deviceScreenWidth = metrics.widthPixels;
            deviceScreenHeight = metrics.heightPixels;
            
	    	imei = t.getDeviceId();
	    	imsi = t.getSubscriberId();
	    	simOperator = t.getSimOperator();
			try{
	            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
	            ApplicationInfo info = manager.getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
	            
	            appVersion = packageInfo.versionName;
	            appVersionCode = ""+packageInfo.versionCode;
			}catch(Exception e){
				e.printStackTrace();
			}
	    }
	    
	    
	    public static QLSystemParams getInstance(Context context){
	    	if(appParams == null)
	    		appParams = new QLSystemParams(context);
	    	return appParams;
	    }


		public String getDeviceType() {
			return deviceType;
		}


		public String getDeviceName() {
			return deviceName;
		}


		public String getDeviceOSVersion() {
			return deviceOSVersion;
		}


		public String getDeviceCountryCode() {
			return deviceCountryCode;
		}

		public String getDeviceLanguage() {
			return deviceLanguage;
		}

		public String getImei() {
			return imei;
		}
		
		public String getImsi() {
			return imsi;
		}

		public String getSimOperator() {
			return simOperator;
		}

		public int getDeviceScreenWidth() {
			return deviceScreenWidth;
		}


		public int getDeviceScreenHeight() {
			return deviceScreenHeight;
		}


		public String getAppVersion() {
			return appVersion;
		}


		public String getAppVersionCode() {
			return appVersionCode;
		}
	  
}
