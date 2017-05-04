package app.utils.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//保存数据utils
public class SharepreferencesUtils {

	private Context context;
	private SharedPreferences sharedpreferences;
	private Editor editor;

	public SharepreferencesUtils(Context context) {
		this.context = context;
		sharedpreferences = context.getSharedPreferences("utils",
				Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
	}

	public int getFirst() {
		return sharedpreferences.getInt("ISFIRST_LOGIN", -1);
	}

	public void setFirst() {
		editor.putInt("ISFIRST_LOGIN", 1);
		editor.commit();
	}

	//loginType 0 账号密码 1 微信登录
	public int getLoginType() {
		return sharedpreferences.getInt("LOGIN_TYPE", 0);
	}

	public void setLoginType(int type) {
		editor.putInt("LOGIN_TYPE", type);
		editor.commit();
	}
	public String getToken() {
		return sharedpreferences.getString("LOGIN_TOKEN", null);
	}

	public void setToken(String token) {
		editor.putString("LOGIN_TOKEN", token);
		editor.commit();
	}


	public String getUserName() {
		return sharedpreferences.getString("username", null);
	}

	public void setUserName(String username) {
		editor.putString("username", username);
		editor.commit();
	}

	public String getPassword() {
		return sharedpreferences.getString("password", null);
	}

	public void setPassword(String psw) {
		editor.putString("password", psw);
		editor.commit();
	}

	public void setRemenber(boolean flag) {
		editor.putBoolean("remenber", flag);
		editor.commit();
	}

	public boolean getRemenber() {
		return sharedpreferences.getBoolean("remenber", false);
	}
	public void setState(boolean safe_state) {
		editor.putBoolean("safe_state", safe_state);
		editor.commit();
	}
	
	public boolean getState() {
		return sharedpreferences.getBoolean("safe_state", false);
	}
	public void setSecond(boolean first) {
		editor.putBoolean("FirstSetting", first);
		editor.commit();
	}
	
	public boolean getSecond() {
		return sharedpreferences.getBoolean("FirstSetting", false);
	}
	public void setJinji(boolean jinjin){
		editor.putBoolean("jinji", jinjin);
		editor.commit();
	}
	public boolean getJinji() {
		return sharedpreferences.getBoolean("jinji", false);
	}

	//*********2.017.3.16 YSF ***********//
	public void setNeedLogin( boolean needLogin ){
		editor.putBoolean("needlogin", needLogin );
		editor.commit();
	}

	public boolean getNeedLogin(){
		return sharedpreferences.getBoolean("needlogin", false);
	}

	public void setWxCode( String wxcode ){
		editor.putString("wxcode", wxcode );
		editor.commit();
	}

	public String getWxCode(){
		return sharedpreferences.getString("wxcode" , "");
	}

	public void setConversion(String key, String value ){
		editor.putString(key, value );
		editor.commit();
	}

	public String getConversion(String key){
		return sharedpreferences.getString(key , "");
	}
}
