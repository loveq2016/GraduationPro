package org.ql.utils.db;

import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharedPreferences implements DBInterface{
	private SharedPreferences sp = null;
	
	public MySharedPreferences(Context context) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public MySharedPreferences(Context context, String name) {
		this(context, name, Context.MODE_PRIVATE);
	}

	public MySharedPreferences(Context context, String name, int mode) {
		sp = context.getSharedPreferences(name, mode);
	}

	/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////
	@Override
	public synchronized boolean contains(String key) {
		return sp.contains(key);
	}

	@Override
	public synchronized Map<String,?> getAll(){
		return sp.getAll();
	}

	@Override
	public synchronized boolean clear() {
		return sp.edit().clear().commit();
	}

	@Override
	public synchronized boolean remove(String key) {
		return sp.edit().remove(key).commit();
	}
	
	@Override
	public boolean isEmpty() {
		return getAll().isEmpty();
	}
	
	/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	@Override
	public synchronized boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	@Override
	public synchronized boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	@Override
	public synchronized float getFloat(String key) {
		return getFloat(key, 0);
	}
	
	@Override
	public synchronized float getFloat(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}
	
	@Override
	public synchronized double getDouble(String key) {
		return getDouble(key,0);
	}

	@Override
	public synchronized double getDouble(String key, double defValue) {
		return sp.getFloat(key, (float) defValue);
	}

	@Override
	public synchronized int getInt(String key) {
		return getInt(key, 0);
	}
	
	@Override
	public synchronized int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	@Override
	public synchronized long getLong(String key) {
		return getLong(key, 0);
	}
	
	@Override
	public synchronized long getLong(String key, long defValue) {
		return sp.getLong(key, defValue);
	}

	@Override
	public synchronized String getString(String key) {
		return getString(key, "");
	}
	
	@Override
	public synchronized String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}
	/////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	/////////////////////////////////////////////PUT//////////////////////////////////////////////////////////
	
	@Override
	public synchronized boolean putBoolean(String key, boolean value) {
		return sp.edit().putBoolean(key, value).commit();
	}

	@Override
	public synchronized boolean putFloat(String key, float value) {
		return sp.edit().putFloat(key, value).commit();
	}
	
	@Override
	public synchronized boolean putDouble(String key, double value) {
		return sp.edit().putFloat(key, (float)value).commit();
	}


	@Override
	public synchronized boolean putInt(String key, int value) {
		return sp.edit().putInt(key, value).commit();
	}

	@Override
	public synchronized  boolean putLong(String key, long value) {
		return sp.edit().putLong(key, value).commit();
	}

	@Override
	public synchronized  boolean putString(String key, String value) {
		return sp.edit().putString(key, value).commit();
	}
	
	/////////////////////////////////////////////Put//////////////////////////////////////////////////////////
	public synchronized String getValue(String key){
		return getString(key,null);
	}

	
	
	
	/**********************************************************************/
	
	public synchronized boolean get(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}

	public synchronized float get(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}

	public synchronized int get(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	public synchronized long get(String key, long defValue) {
		return sp.getLong(key, defValue);
	}

	public synchronized String get(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public synchronized boolean put(String key, boolean value) {
		return sp.edit().putBoolean(key, value).commit();
	}

	public synchronized boolean put(String key, float value) {
		return sp.edit().putFloat(key, value).commit();
	}

	public synchronized boolean put(String key, int value) {
		return sp.edit().putInt(key, value).commit();
	}

	public synchronized boolean put(String key, long value) {
		return sp.edit().putLong(key, value).commit();
	}

	public synchronized boolean put(String key, String value) {
		return sp.edit().putString(key, value).commit();
	}
	
	
	public synchronized boolean put(String key, Object value) {
		boolean r = false;
		if(value instanceof Boolean)
			r = putBoolean(key, (Boolean)value);
		else if(value instanceof Float)
			r = putFloat(key, (Float)value);
		else if(value instanceof Integer)
			r = putInt(key, (Integer)value);
		else if(value instanceof Long)
			r = putLong(key, (Long)value);
		else if(value instanceof String)
			r = putString(key, (String)value);
		return r;
	}
	
}
