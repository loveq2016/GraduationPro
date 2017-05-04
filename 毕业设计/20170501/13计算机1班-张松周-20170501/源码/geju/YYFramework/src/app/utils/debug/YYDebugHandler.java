package app.utils.debug;

import android.content.Context;
import app.utils.common.Listener;

/**
 * 
 * @author SiuJiYung
 *  
 */
public class YYDebugHandler {

	private static YYDebugHandler __DebugHandler;
	private Listener<Integer, String> configSyncLinstener;
	
	public static YYDebugHandler getShareInstance(){
		if (__DebugHandler == null) {
			__DebugHandler = new YYDebugHandler();
		}
		return __DebugHandler;
	}
	
	private YYDebugHandler(){
		
	}
	
	public void setConfigSyncListener(Listener<Integer, String> l){
		configSyncLinstener = l;
	}
	
	/**
	 * 初始化第三方数据收集器
	 * @param context
	 */
	public void initDataRecorder(Context context){
		
	}
	
	/**
	 * Activity暂停时调用
	 * @param context
	 */
	public void onPause(Context context){
		
	}
	
	/**
	 * Activity恢复时调用
	 * @param context
	 */
	public void onResume(Context context){
		
	}
	
	public String getConfigParamsForKey(String key){
		return null;
	}
	
	/**
	 * 上传错误报告
	 * @param context
	 * @param msg
	 */
	public  void reportError(Context context,String msg){
		sendErrorReport(context, msg);
	}
	
	/**
	 * 上传错误报告
	 * @param context
	 * @param exception
	 */
	public  void reportError(Context context,Exception exception){
		reportError(context, exception.getStackTrace().toString());
	}
	
	/**
	 * 同步配置
	 */
	private void syncConfig(){
		
	}
	
	/**
	 * 在这里接入第三方bug收集接口
	 * @param context
	 * @param msg
	 */
	private void sendErrorReport(Context context,String msg){
		
	}
	
	
	
	
}
