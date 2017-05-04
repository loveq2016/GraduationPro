package app.config.http;

import org.json.simple.JSONObject;
import org.ql.utils.QLJsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class YYResponseData {
	private String msg;
	private String token;
	private String errorMsg;
	private String error_msg;
	private Object otherInfo;
	private String souceJsonString;
	private boolean success;
	private int code;
	private int wp_error_code;
	private String error;
	private String result;
	private String wp_error_msg;

	private String nickname ;
	private String headimgurl ;

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	public void setSouceJsonString(String souceJsonString) {
		this.souceJsonString = souceJsonString;
	}

	public String getWp_error_msg() {
		return wp_error_msg;
	}

	public void setWp_error_msg(String wp_error_msg) {
		this.wp_error_msg = wp_error_msg;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getErrorMsg() {
		String _tmp = error_msg!=null?error_msg:errorMsg;
		_tmp = _tmp == null?wp_error_msg:_tmp;
		return _tmp;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		this.error_msg = errorMsg;
		this.wp_error_msg = errorMsg;
	}
	public Object getOtherInfo() {
		return otherInfo;
	}
	public void setOtherInfo(Object otherInfo) {
		this.otherInfo = otherInfo;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getSouceJsonString() {
		return souceJsonString;
	}
	
	/**
	 * 解析响应字符串
	 * @param jsonString
	 * @return
	 */
	public static YYResponseData parseJsonString(String jsonString){
		if (jsonString == null || jsonString.isEmpty()) {
			return null;
		}
		YYResponseData data = null;
		try {
			Gson gson = new Gson();
			data  = gson.fromJson(jsonString, YYResponseData.class);
			data.souceJsonString = jsonString;
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	public <T> T parseData(String rootKeyName,TypeToken<T> typeToken){
		JSONObject jsonObj = QLJsonUtil.doJSONObject(souceJsonString);
		if (jsonObj != null) {
			Object tmpRootValue = jsonObj.get(rootKeyName);
			if (tmpRootValue != null) {
				try {
					Gson gson = new Gson();
					String tmp_json = gson.toJson(tmpRootValue);
					return gson.fromJson(tmp_json, typeToken.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getWp_error_code() {
		return wp_error_code;
	}
	public void setWp_error_code(int wp_error_code) {
		this.wp_error_code = wp_error_code;
	}
	
	
}
