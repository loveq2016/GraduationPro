package app.utils.db.sqlite;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DbMap{
	public static final String DEFAULT_DB_NAME = "DbMap.db";
	public static final String DEFAULT_DB_TABLE = "map_table";
	
	private String dbTable = DEFAULT_DB_TABLE;
	
	//======================SQLiteOpenHelper===========================//
	 /**
     * key: dbName
     */
    private static HashMap<String, DbMap> daoMap = new HashMap<String, DbMap>();
    
    private SQLiteDatabase db;
    
	public static class DaoConfig {
		
		private String dbTable;
		
		private String dbName;

		private String dbDir;

		private Context context;
		
		public DaoConfig(Context context){
			this.context = context;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}
		
		public String getDbTable() {
			return dbTable;
		}

		public void setDbTable(String dbTable) {
			this.dbTable = dbTable;
		}

		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public String getDbDir() {
			return dbDir;
		}

		public void setDbDir(String dbDir) {
			this.dbDir = dbDir;
		}
	}
	
	public DbMap(DaoConfig daoConfig) {
		if(!TextUtils.isEmpty(daoConfig.getDbDir())){
			this.db = createDbFileOnSDCard(daoConfig.getDbDir(), daoConfig.getDbName());
		}
		
		if(this.db == null)
			this.db = new SQLiteHelper(daoConfig.getContext(), daoConfig.getDbName(),daoConfig.getDbTable()).getWritableDatabase();
		
		this.dbTable = daoConfig.getDbTable();
		daoMap.put(daoConfig.getDbTable(), this);
	}
	
	public static DbMap create(Context context) {
		return DbMap.create(context,DEFAULT_DB_NAME,DEFAULT_DB_TABLE);
	}
	
	public static DbMap create(Context context,String dbTable) {
		return DbMap.create(context, DEFAULT_DB_NAME, dbTable);
	}
	
	/**
	 * @hide
	 */
	private static DbMap create(Context context, String dbName,String dbTable) {
		return DbMap.create(context,null, dbName, dbTable);
	}
	
	/**
	 * @hide
	 */
	private static DbMap create(Context context, String dbDir, String dbName,String dbTable) {
		if (TextUtils.isEmpty(dbName)) {
			throw new RuntimeException("dbName不可为NULL");
		}
		
		if (TextUtils.isEmpty(dbTable)) {
			throw new RuntimeException("dbTable不可为NULL");
		}
		
		DbMap dao = null;
		dao = daoMap.get(dbTable);
		if (dao == null) {
			DaoConfig config = new DaoConfig(context);
	         config.setDbName(dbName);
	         config.setDbDir(dbDir);
	         config.setDbTable(dbTable);
			dao = new DbMap(config);
		}
        return dao;
	 }
	 
	/**
	 * 创建本地数据库
	 * @param sdcardPath
	 * @param dbfilename
	 * @return
	 */
	private SQLiteDatabase createDbFileOnSDCard(String sdcardPath,String dbfilename) {
		File dbf = new File(sdcardPath, dbfilename);
		if (!dbf.exists()) {
			try {
				if (dbf.createNewFile()) {
					return SQLiteDatabase.openOrCreateDatabase(dbf, null);
				}
			} catch (IOException e) {
			}
		} else {
			return SQLiteDatabase.openOrCreateDatabase(dbf, null);
		}
		return null;
	}
	
	private class SQLiteHelper extends SQLiteOpenHelper{
		public SQLiteHelper(Context context,String dbName,String dbTable) {
			super(context, dbName, null, 1);
			
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name='"+dbTable+"' order by name",null);
			int count = c.getCount();
			if(count <= 0){
				db.execSQL("CREATE TABLE "+dbTable+" ( _id INTEGER PRIMARY KEY AUTOINCREMENT,key TEXT,value TEXT )");
			}
			c.close();
			db.close();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}
	//======================SQLiteOpenHelper===========================//
	
	/////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	
	public boolean getBoolean(String key){
		return getBoolean(key,false);
	}

	public boolean getBoolean(String key,boolean defValue){
		String v = getValue(key);
		return TextUtils.isEmpty(v) ? defValue : Boolean.parseBoolean(v);
	}

	public int getInt(String key) {
        return getInt(key,0);
    }
	
    public int getInt(String key,int defValue) {
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Integer.parseInt(v);
    }

    public double getDouble(String key){
        return getDouble(key,0);
    }

    public double getDouble(String key,double defValue){
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Double.parseDouble(v);
    }

    public float getFloat(String key){
    	return getFloat(key,0);
    }

    public float getFloat(String key,float defValue){
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Float.parseFloat(v);
    }

    public long getLong(String key) {
        return getLong(key,0);
    }

    public long getLong(String key,long defValue)  {
    	String v = getValue(key);
    	return TextUtils.isEmpty(v) ? defValue : Long.parseLong(v);
    }

    public String getString(String key){
    	return getString(key,"");
    }

    public String getString(String key,String defValue){
    	String v = getValue(key);
        return TextUtils.isEmpty(v) ? defValue : v;
    }
	
    /////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	
	
	
    /////////////////////////////////////////////PUT//////////////////////////////////////////////////////////
    

	public boolean putBoolean(String key,boolean value){
		return putValue(key, Boolean.toString(value));
	}

	public boolean putInt(String key,int value){
		return putValue(key, Integer.toString(value));
	}

	public boolean putDouble(String key,double value){
		return putValue(key, Double.toString(value));
	}

	public boolean putFloat(String key,float value){
		return putValue(key, Double.toString(value));
	}

	public boolean putLong(String key,long value){
		return putValue(key, Long.toString(value));
	}

	public boolean putString(String key,String value){
		return putValue(key, value);
	}
	
    /////////////////////////////////////////////PUT//////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////
	
	private Lock writeLock = new ReentrantLock();
	private void lock() {
		writeLock.lock();
	}

	private void unlock() {
		writeLock.unlock();
	}
	
	private String getValue(String key){
		return getValue(key,"");
	}
	
	private String getValue(String key,String defValue){
		try{
			lock();
			if(TextUtils.isEmpty(key))
				return "";
			Cursor c = db.rawQuery("SELECT value FROM "+dbTable+" WHERE key='"+key+"'",null);
			if (c.moveToNext()) {
				return c.getString(0);
			}
			return defValue;
		}finally{
			unlock();
		}
	}
	
	private boolean putValue(String key,String value){
		if(TextUtils.isEmpty(key))
			return false;
		try{
			lock();
			Cursor c = db.rawQuery("SELECT * FROM "+dbTable+" WHERE key='"+key+"'",null);
			int count = c.getCount();
			boolean result = false;
			if(count > 0){
				//更新
				ContentValues cv = new ContentValues();
				cv.put("value", value);
				result =  db.update(dbTable, cv, "key=?", new String[]{key}) > 0;
			}else{
				//插入
				ContentValues cv = new ContentValues();
				cv.put("key", key);
				cv.put("value", value);
				result =  db.insert(dbTable, null, cv) > 0;
			}
			return result;
		}finally{
			unlock();
		}
	}

	public boolean remove(String key){
		if(TextUtils.isEmpty(key))
			return false;
		try{
			lock();
			boolean result = db.delete(dbTable, "key=?",new String[]{key}) > 0;
			return result;
		}finally{
			unlock();
		}
	}

	public boolean clear(){
		try{
			lock();
			return db.delete(dbTable, null,null) > 0;
		}finally{
			unlock();
		}
	}

	public Map<String,String> getAll(){
		try{
			lock();
			Map<String,String> map = new HashMap<String,String>();
			Cursor c = db.rawQuery("SELECT * FROM "+dbTable, null);
			while(c.moveToNext()){
				String key = c.getString(1);
				String value = c.getString(2);
				map.put(key, value);
			}
			return map;
		}finally{
			unlock();
		}
	}

	public List<DbMapEntity> getAllEntity(){
		try{
			lock();
			List<DbMapEntity> list = new ArrayList<DbMapEntity>();
			Cursor c = db.rawQuery("SELECT * FROM "+dbTable, null);
			while(c.moveToNext()){
				DbMapEntity entity = new DbMapEntity();
				entity.setId(c.getInt(0));
				entity.setKey(c.getString(1));
				entity.setValue(c.getString(2));
				list.add(entity);
			}
			return list;
		}finally{
			unlock();
		}
	}
	
	public boolean contains(String key){
		if(TextUtils.isEmpty(key))
			return false;
		try{
			lock();
			Cursor c = db.rawQuery("SELECT * FROM "+dbTable+" WHERE key=?", new String[]{key});
			int count = c.getCount();
			return count > 0 ? true : false;
		}finally{
			unlock();
		}
	}
	
	public boolean isEmpty() {
		try{
			lock();
			Cursor c = db.rawQuery("SELECT COUNT * FROM "+dbTable,null);
			int count = c.getCount();
			return count == 0;
		}finally{
			unlock();
		}
	}
	
}
