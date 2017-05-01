package org.ql.utils.cache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.ql.utils.QLFileUtil;
import org.ql.utils.QLStringUtils;
import org.ql.utils.debug.QLLog;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class QLFileCacheUtils {
	private Context context;
	private String bufferParent;
	private String pacName;
	
	public QLFileCacheUtils(Context context){
		this.context = context;
		pacName=context.getPackageName();
	}
	
	/**
	 * SD卡缓存路径
	 * @param imgBufferParent
	 */
	public void setBufferParent(String imgBufferParent) {
		this.bufferParent = imgBufferParent;
	}
	
	public String getBufferParent() {
		if(TextUtils.isEmpty(bufferParent)){
			String status = Environment.getExternalStorageState();
			boolean isSDCardExist = status.equals(Environment.MEDIA_MOUNTED);
			if (isSDCardExist) {
				String sdcard = Environment.getExternalStorageDirectory().toString();
				bufferParent = new File(sdcard,"android/data/"+pacName+"/cache/network").getAbsolutePath();		}
		    }
			return bufferParent;
	}
	
	public String getRealPath(String key){
		String path = getBufferParent()+"/"+key+".ql";
		//QLLog.e(path);
		return path;
	}
	
	public boolean putString(String url, String key,String value,int timeOut){
		if(null==value||key==null||timeOut<0){
			return false;
		}
		QLLog.e("保存缓存 ：timeOut-->"+timeOut + "s-->"+url);
		if(value.length()<30){
			timeOut = 0;
		}
		Map<String, String> cache = new HashMap<String, String>();
		cache.put("timeOut", Integer.toString(timeOut));
		cache.put("content", value);
		cache.put("createTime", Long.toString(System.currentTimeMillis()));
		Gson gson = new Gson();
		QLFileUtil.writeStringToFile(getRealPath(key), gson.toJson(cache));
		return true;
	}
	
	public boolean containsKey(String key){
		String value = QLFileUtil.readStringFromFile(getRealPath(key), false);
		return value==null?false:true;
	}
	
	public boolean isTimeOut(String key){
		if(key==null||!QLFileUtil.isExists(getRealPath(key))){
			return true;
		}
		String value = QLFileUtil.readStringFromFile(getRealPath(key), false);
		if(QLStringUtils.isEmpty(value)){
			return true;
		}
		String strTimeOut = null;
		String strCreateTime = null;
		try {
			Gson gson = new Gson();
			Map<String, String> cache = gson.fromJson(value,  new TypeToken<Map<String, String>>(){}.getType());
			strTimeOut = cache.get("timeOut");
			strCreateTime = cache.get("createTime");
		} catch (Exception e) {
			
		}
		
		if(!QLStringUtils.isEmpty(strCreateTime)&&!QLStringUtils.isEmpty(strTimeOut)){
			int timeOut = Integer.parseInt(strTimeOut);
			long createTime = Long.parseLong(strCreateTime);
			if(System.currentTimeMillis()<createTime+timeOut*1000){
				return false;
			}
		}
		return true;
	}
	
	public String getString(String key){
		if(key==null||!QLFileUtil.isExists(getRealPath(key))){
			return null;
		}
		String value = QLFileUtil.readStringFromFile(getRealPath(key), false);
		try {
			Gson gson = new Gson();
			Map<String, String> cache = gson.fromJson(value,  new TypeToken<Map<String, String>>(){}.getType());
			return cache.get("content");
		} catch (Exception e) {
		}
		return null;
	}
}
