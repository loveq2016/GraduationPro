package app.utils.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.ql.utils.debug.QLLog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import app.config.YYAppConfig;
import app.utils.common.Public;


public class DateTimeSynModel extends BroadcastReceiver {

	private final String SYNC_DATETIME_ENABLE = "SYNC_DATETIME_ENABLE";
	private final String SYNC_DATE_FLAG = "OPEN";
	public static final String SCREEN_STATUS_KEY = "SCREEN_STATUS_KEY";
	private Method mReflectScreenState;
	private PowerManager manager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!isScreenOn(context)) {
			QLLog.i("DateTimeSyn", "screen off.");
			return;
		}
		boolean syncDate = false;
//		String syncDateEnable = MobclickAgent.getConfigParams(context, SYNC_DATETIME_ENABLE);
//		if (syncDateEnable != null) {
//			syncDate = SYNC_DATE_FLAG.equals(syncDateEnable)?true:false;
//		}
			
		if ( !YYAppConfig.shareInstance().isSyncTime()) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronizeDateTime();
			}
		}).start();
	}

	private boolean isScreenOn(Context context){
		if (mReflectScreenState == null) {
			try {  
	            mReflectScreenState = PowerManager.class.getMethod("isScreenOn",  
	                    new Class[] {});  
	        } catch (NoSuchMethodException nsme) {
	        	return true;
	        }
		}
		if (manager == null) {
			manager = (PowerManager)context.getSystemService(Activity.POWER_SERVICE);
		}
        Boolean screenOn = true;
		try {
			screenOn = (Boolean)mReflectScreenState.invoke(manager);
		} catch (Exception e) {
			screenOn = true;
		} 
        return screenOn;
	}
	
	/**
     * 同步时间
     * @throws IOException 
     */
    private void synchronizeDateTime(){
		try{
			long startRequestTime = SystemClock.elapsedRealtime();
			String requestURL = "http://m.163.com";//TYInterfaceList.STR_STANDAR_DATETIME;
			URL url=new URL(requestURL);
	        URLConnection uc=url.openConnection();
	        uc.setConnectTimeout(3000);
	        uc.connect();
	        long ld=uc.getDate();
	        InputStream is = uc.getInputStream();
	        is.close();
	        Date dtDate = new Date(ld);
	        if (dtDate.getYear()<(2014-1900)) {
	        	Public.synTimeListener.onCallBack(0L, null);
	        	return;
	        }
//	        String dtstrString = Public.formatter.format(dtDate);
//	        QLLog.i("服务器标准时间", ""+dtstrString);
	        long offset = ld - new Date().getTime();
	        Public.synTimeListener.onCallBack(offset, null);
//	        Date localDate = Public.getDateTimeNow();
//	        String localDateString = Public.formatter.format(localDate);
//	        QLLog.i("本地运算时间", localDateString);
//	        long localMatchingTime = System.currentTimeMillis();
//	        Date localMatchingDate = new Date(localMatchingTime);
//	        String localMatchingDateString = Public.formatter.format(localMatchingDate);
//	        QLLog.i("本地机器时间", localMatchingDateString);
    	}catch (Exception e) {
			e.printStackTrace();
			Public.synTimeListener.onCallBack(0L, null);
    	}
    }
}
