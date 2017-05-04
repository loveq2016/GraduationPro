package app.utils.helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IInterface;
import app.utils.db.SQLEntity;
import app.utils.db.SQLiteHelper;
import app.utils.sqllite.SqliteHelp;

/**
*
* SiuJiYung create at 2016年6月22日 下午3:56:26
*
*/

public class PropertySaveHelper {
	private static PropertySaveHelper _helper;
	private static final String SP_NAME = "SP_PROPERTY";
	
	public static PropertySaveHelper getHelper(){
		if (_helper == null) {
			_helper = new PropertySaveHelper();
		}
		return _helper;
	}
	private Context mContext;
	private SharedPreferences sp;
	private Editor editor;
	
	private PropertySaveHelper(){
		
	}
	
	public void setContext(Context context){
		mContext = context;
		sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public void save(Object object ,String key){
		if (object == null && key != null) {
			editor.remove(key);
			editor.commit();
			return;
		}
		try {
			Gson gson = new Gson();
			String jsonString = gson.toJson(object);
			editor.putString(key, jsonString);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveString(String value,String key){
		editor.putString(key, value);
		editor.commit();
	}
	
	public void saveIntValue(int value,String key){
		editor.putInt(key, value);
		editor.commit();
	}
	
	public int intValueForKey(String key,int defaultValue){
		return sp.getInt(key, defaultValue);
	}
	
	public String stringForKey(String key){
		return sp.getString(key, null);
	}
	
	public <T> T objectForKey(String key){
		String json = sp.getString(key, null);
		if (json == null) {
			return null;
		}
		try {
			Gson gson = new Gson();
			T t = gson.fromJson(json, new TypeToken<T>(){}.getType());
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
