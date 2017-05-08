package app.config;

import java.util.Map;

import org.ql.utils.QLFileUtil;

import android.content.Context;

/**
*
* SiuJiYung create at 2016-5-31 下午4:57:30
*
*/
public class YYAppConfig {
	public static final String HANVAN_KEY = "c153d134-d483-47e3-b6d2-6c4155739b70";
	public static final String UCLOUND_GCI = "ucloud.publish.gzyueyun.comb73fb497";
	public static final String UCLOUND_PUSH_STREAM = "rtmp://publish.gzyueyun.com/ucloud/";
	public static final String UCLOUND_PLAY_STREAM = "rtmp://rtmp.gzyueyun.com/ucloud/";
	private static final String DEFAULT_SETTING_JSON_FILE_NAME = "AppConfig.json";
	private static YYAppConfig _shareInstance;
	private boolean debugMod;
	private boolean synTime;
	private Map<String, String> appDefaultSettings;
	
	private YYAppConfig(){
		
	}
	
	public static YYAppConfig shareInstance(){
		if (_shareInstance == null) {
			_shareInstance = new YYAppConfig();
		}
		return _shareInstance;
	}
	
	public void init(Context context){
//		QLFileUtil.readStringFromFile(fileName, addEnterLindEnd)
	}
	
	public boolean isSyncTime(){
		return synTime;
	}
	
	public void setSyncTime(boolean sync){
		synTime = sync;
	}
	
	public boolean isDebugModel(){
		return debugMod;
	}
	
	public void setDebugModel(boolean debug){
		debugMod = debug;
	}
}
