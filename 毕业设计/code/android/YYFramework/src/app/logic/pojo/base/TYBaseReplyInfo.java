package app.logic.pojo.base;

import java.lang.reflect.Type;
import java.util.List;

import android.text.TextUtils;
import app.utils.common.Public;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class TYBaseReplyInfo{
	
	
	public class TYListDataCollector<T>{
		public List<T>	rows;
		public int count;
		public int totals;
	}
	
	public static TYBaseReplyInfo parseToObject(String replayString){
		if (replayString == null) {
			return null;
		}
		Gson gson = Public.getGson();
		TYBaseReplyInfo _info = null;
		try {
			_info = gson.fromJson(replayString, TYBaseReplyInfo.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return _info;
	}
	
	public static <T> T getResultInfo(TYBaseReplyInfo info,Class<T> t){
		if (info == null || info.getResult() == null) {
			return null;
		}
		String resultString = info.getResult();
		try {
			Gson gson = Public.getGson();
			T _info = gson.fromJson(resultString, t);
			return _info;
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> T toType(Class<T> t){
		String resultString = getResult();
		if (TextUtils.isEmpty(resultString)) {
			return null;
		}
		try {
			Gson gson = Public.getGson();
			T _info = gson.fromJson(resultString, new TypeToken<T>(){}.getType());
			return _info;
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int code;
	private String msg;
	private String result;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
