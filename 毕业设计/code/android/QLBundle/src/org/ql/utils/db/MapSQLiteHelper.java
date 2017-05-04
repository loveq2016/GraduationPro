package org.ql.utils.db;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ql.utils.debug.QLLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class MapSQLiteHelper implements DBInterface{
	private final static String tag = MapSQLiteHelper.class.getSimpleName();
	
	private static final String DEFAULT_TABLE = "default_sqlite";
	private static final int VERSION = 1;
	private String table = DEFAULT_TABLE;
	private String pacName;
	
	
	private SQLiteHelper sql;
	//======================SQLiteOpenHelper===========================//
	
	public MapSQLiteHelper(Context context) {
		this(context, DEFAULT_TABLE);
		pacName=context.getPackageName();
	}
	
	public MapSQLiteHelper(Context context,String table) {
		this.table = table;
		sql = new SQLiteHelper(context, table);
		pacName=context.getPackageName();
	}
	
	
	
	private class SQLiteHelper extends SQLiteOpenHelper{
		private boolean close = false;
		public SQLiteHelper(Context context,String table) {
			super(context, pacName+"_map_sqlite.db", null, VERSION);
			
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name='"+table+"' order by name",null);
			int count = c.getCount();
			QLLog.e(tag, "count="+count);
			if(count <= 0){
				db.execSQL("CREATE TABLE "+table+" ( _id INTEGER PRIMARY KEY AUTOINCREMENT,k TEXT,v TEXT )");
			}
			closeDb(db, c);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}

		@Override
		public synchronized void close() {
			super.close();
			close = true;
		}
		
		public synchronized boolean isClose(){
			return close;
		}
		
	}
	
	
	
	//======================SQLiteOpenHelper===========================//
	
	
	
	/////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	private synchronized String getValue(String key){
		return getValue(key,"");
	}
	
	private synchronized String getValue(String key,String defValue){
		if(TextUtils.isEmpty(key))
			return "";
		SQLiteDatabase db = sql.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT v FROM "+table+" WHERE k='"+key+"'",null);
		if (c.moveToNext()) {
			return c.getString(0);
		}
		closeDb(db, c);
		return defValue;
	}
	
	@Override
	public synchronized boolean getBoolean(String key){
		return getBoolean(key,false);
	}

	@Override
	public synchronized boolean getBoolean(String key,boolean defValue){
		String v = getValue(key);
		return TextUtils.isEmpty(v) ? defValue : Boolean.parseBoolean(v);
	}

	@Override
	public synchronized int getInt(String key) {
        return getInt(key,0);
    }
	
    public synchronized int getInt(String key,int defValue) {
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Integer.parseInt(v);
    }

	@Override
    public synchronized double getDouble(String key){
        return getDouble(key,0);
    }

	@Override
    public synchronized double getDouble(String key,double defValue){
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Double.parseDouble(v);
    }

	@Override
    public synchronized float getFloat(String key){
    	return getFloat(key,0);
    }

	@Override
    public synchronized float getFloat(String key,float defValue){
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Float.parseFloat(v);
    }

	@Override
    public synchronized long getLong(String key) {
        return getLong(key,0);
    }

	@Override
    public synchronized long getLong(String key,long defValue)  {
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Long.parseLong(v);
    }

	@Override
    public synchronized String getString(String key){
    	return getString(key,"");
    }

	@Override
    public synchronized String getString(String key,String defValue){
    	String v = getValue(key);
        return TextUtils.isEmpty(v) ? defValue : v;
    }
	
    /////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	
	
	
    /////////////////////////////////////////////PUT//////////////////////////////////////////////////////////
    
	private synchronized boolean putValue(String key,String value){
		if(TextUtils.isEmpty(key))
			return false;
		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT k FROM "+table+" WHERE k='"+key+"'",null);
		int count = c.getCount();
		boolean result = false;
		if(count > 0){
			//更新
			ContentValues cv = new ContentValues();
			cv.put("v", value);
			result =  db.update(table, cv, "k=?", new String[]{key}) > 0;
		}else{
			//插入
			ContentValues cv = new ContentValues();
			cv.put("k", key);
			cv.put("v", value);
			result =  db.insert(table, null, cv) > 0;
		}
		closeDb(db, c);
		return result;
	}

	@Override
	public synchronized boolean putBoolean(String key,boolean value){
		return putValue(key, Boolean.toString(value));
	}

	@Override
	public synchronized boolean putInt(String key,int value){
		return putValue(key, Integer.toString(value));
	}

	@Override
	public synchronized boolean putDouble(String key,double value){
		return putValue(key, Double.toString(value));
	}

	@Override
	public synchronized boolean putFloat(String key,float value){
		return putValue(key, Double.toString(value));
	}

	@Override
	public synchronized boolean putLong(String key,long value){
		return putValue(key, Long.toString(value));
	}

	@Override
	public synchronized boolean putString(String key,String value){
		return putValue(key, value);
	}
	
    /////////////////////////////////////////////PUT//////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////

	@Override
	public synchronized boolean remove(String key){
		if(TextUtils.isEmpty(key))
			return false;
		SQLiteDatabase db = sql.getWritableDatabase();
		boolean result = db.delete(table, "k=?",new String[]{key}) > 0;
		closeDb(db, null);
		return result;
	}
	

	@Override
	public synchronized boolean clear(){
		SQLiteDatabase db = sql.getWritableDatabase();
		boolean result = db.delete(table, null,null) > 0;
		closeDb(db, null);
		return result;
	}

	@Override
	public synchronized Map<String,String> getAll(){
		Map<String,String> map = new HashMap<String,String>();
		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM "+table, null);
		while(c.moveToNext()){
			String key = c.getString(1);
			String value = c.getString(2);
			map.put(key, value);
		}
		closeDb(db, c);
		return map;
	}

	public synchronized List<MapSQLiteEntity> getAllEntity(){
		List<MapSQLiteEntity> list = new ArrayList<MapSQLiteEntity>();
		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM "+table, null);
		while(c.moveToNext()){
			MapSQLiteEntity entity = new MapSQLiteEntity();
			entity.setId(c.getInt(0));
			entity.setKey(c.getString(1));
			entity.setValue(c.getString(2));
			list.add(entity);
		}
		closeDb(db, c);
		return list;
	}
	
	@Override
	public synchronized boolean contains(String key){
		if(TextUtils.isEmpty(key))
			return false;
		SQLiteDatabase db = sql.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT k FROM "+table+" WHERE k=?", new String[]{key});
		int count = c.getCount();
		closeDb(db, c);
		return count > 0 ? true : false;
	}
	
	@Override
	public synchronized boolean isEmpty() {
		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT COUNT(_id) FROM "+table,null);
		String count = null;
		if (c.moveToNext()) {
			count = c.getString(0);
		}
		boolean result = count == null || "0".equals(count);
		closeDb(db, c);
		return result;
	}
	
	
	/**
	 * 释放数据库资源
	 **/
	private synchronized void closeDb(SQLiteDatabase db, Cursor c) {
		if (c != null) {
			c.close();
			c = null;
		}
		if (db != null) {
			db.close();
			db = null;
		}
	}
	
	public synchronized void close(){
		if(sql != null){
			sql.close();
			sql = null;
		}
	}
	
	public synchronized boolean isClose(){
		if(sql != null){
			return sql.isClose();
		}
		return true;
	}
	
	/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////
    
	
}
