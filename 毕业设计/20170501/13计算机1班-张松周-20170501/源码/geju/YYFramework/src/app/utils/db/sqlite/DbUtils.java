package app.utils.db.sqlite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
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
import android.util.Log;
/**
 * 数据库助手类  android的数据库实现了单表的增删改查就够了
 * @author duohuo-jinghao
 */
public class DbUtils {

	private SQLiteDatabase db;
	
    /**
     * key: dbName
     */
    private static HashMap<String, DbUtils> daoMap = new HashMap<String, DbUtils>();
    
	private DbUtils(DaoConfig daoConfig) {
		if(!TextUtils.isEmpty(daoConfig.getDbDir())){
			this.db = createDbFileOnSDCard(daoConfig.getDbDir(), daoConfig.getDbName());
		}
		
		if(this.db == null)
			this.db = new SqliteDbHelper(daoConfig.getContext(), daoConfig.getDbName(), 1).getWritableDatabase();
	}
	
    public static DbUtils create(Context context) {
        return create(context, "DbUtils.db");
    }

    public static DbUtils create(Context context, String dbName) {
        return DbUtils.create(context,null,dbName);
    }

    public static DbUtils create(Context context, String dbDir, String dbName) {
    	if(TextUtils.isEmpty(dbName)){
    		throw new RuntimeException("dbName不可为NULL");
    	}
    	
    	DbUtils dao = null;
		dao = daoMap.get(dbName);
		if (dao == null) {
			DaoConfig config = new DaoConfig();
	         config.setContext(context);
	         config.setDbName(dbName);
	         config.setDbDir(dbDir);
			dao = new DbUtils(config);
			daoMap.put(config.getDbName(), dao);
		}
        return dao;
    }
    
    static class DaoConfig{
    	
        private String dbName; 
        
        private String dbDir;
        
        private Context context;
        
		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
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
	
	private Lock writeLock = new ReentrantLock();
	private void lock() {
		writeLock.lock();
	}

	private void unlock() {
		writeLock.unlock();
	}
	
	/**
	 * 保存
	 * @param obj
	 */
	@Deprecated
	public void save(Object obj) {
		if (obj == null)
			return;
		try{
			lock();
			checkOrCreateTable(obj.getClass());
			SqlProxy proxy = SqlProxy.save(obj);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	/**
	 * 保存
	 * @param obj
	 */
	public long insert(Object obj) {
		if (obj == null)
			return 0;
		try{
			lock();
			checkOrCreateTable(obj.getClass());
			EntityInfo entity=EntityInfo.build(obj.getClass());
			ContentValues cv = SqlProxy.insert(obj);
			return db.insert(entity.getTable(), null, cv);
		}finally{
			unlock();
		}
	}
	
	/**
	 * 更新
	 * @param obj
	 */
	public void update(Object obj) {
		if (obj == null)
			return;
		try{
			lock();
			checkOrCreateTable(obj.getClass());
			SqlProxy proxy = SqlProxy.update(obj);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	/**
	 * 更新
	 * @param obj
	 * @param where
	 * @param whereargs
	 */
	public void update(Object obj,String where,Object... whereargs){
		if (obj == null)
			return;
		try{
			lock();
			checkOrCreateTable(obj.getClass());
			SqlProxy proxy = SqlProxy.update(obj,where,whereargs);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	public void update(Object obj,WhereBuilder where){
		if (obj == null)
			return;
		try{
			lock();
			checkOrCreateTable(obj.getClass());
			SqlProxy proxy = SqlProxy.update(obj,where);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	/**
	 * 更新
	 * @param clazz
	 * @param values
	 * @param where
	 * @param whereargs
	 */
	public void update(Class<?> clazz,Map<String,Object> values,String where,Object... whereargs){
		try{
			lock();
			checkOrCreateTable(clazz);
			SqlProxy proxy = SqlProxy.update(clazz,values,where,whereargs);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	public void update(Class<?> clazz,Map<String,Object> values,WhereBuilder where){
		try{
			lock();
			checkOrCreateTable(clazz);
			SqlProxy proxy = SqlProxy.update(clazz,values,where);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}

	/**
	 * 删除
	 * @param obj
	 */
	public void deleteAll(Class<?> clazz) {
		try{
			lock();
			checkOrCreateTable(clazz);
			SqlProxy proxy = SqlProxy.deleteAll(clazz);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	/**
	 * 删除
	 * @param obj
	 */
	public void delete(Object obj) {
		if (obj == null)
			return;
		try{
			lock();
			checkOrCreateTable(obj.getClass());
			SqlProxy proxy = SqlProxy.delete(obj);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	public void delete(Class<?> clazz,String where,Object... whereargs){
		try{
			lock();
			checkOrCreateTable(clazz);
			SqlProxy proxy = SqlProxy.delete(clazz,where,whereargs);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	public void delete(Class<?> clazz,WhereBuilder where){
		try{
			lock();
			checkOrCreateTable(clazz);
			SqlProxy proxy = SqlProxy.delete(clazz,where);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	public void delete(Class<?> clazz,Object pkvalue){
		try{
			lock();
			checkOrCreateTable(clazz);
			SqlProxy proxy = SqlProxy.delete(clazz,pkvalue);
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	/**
	 * 执行
	 * @param proxy
	 */
	public void execProxy(SqlProxy proxy) {
		try{
			lock();
			db.execSQL(proxy.getSql(), proxy.paramsArgs());
		}finally{
			unlock();
		}
	}
	
	public void execSQL(String sql, Object[] bindArgs) {
		try{
			lock();
			db.execSQL(sql,bindArgs);
		}finally{
			unlock();
		}
	}
	
	public void execSQL(String sql) {
		try{
			lock();
			db.execSQL(sql);
		}finally{
			unlock();
		}
	}
	
	private int count(Class<?> clazz,SqlProxy proxy) {
		Cursor cursor = null;
		try {
			lock();
			checkOrCreateTable(clazz);
			cursor = db.rawQuery(proxy.getSql(), proxy.paramsArgs());
			if(cursor != null)
				return cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null){
				cursor.close();
				cursor = null;
			}
			unlock();
		}
		return 0;
	}
	
	public int count(Class<?> clazz, String where, Object... whereargs) {
		SqlProxy proxy=SqlProxy.select(clazz, where, whereargs);
		return count(clazz,proxy);
	}
	
	public int count(Class<?> clazz, WhereBuilder where) {
		SqlProxy proxy=SqlProxy.select(clazz, where);
		return count(clazz,proxy);
	}
	
	/**
	 * 查询
	 * @param proxy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> queryList(Class<T> clazz,SqlProxy proxy) {
		Cursor cursor = null;
		try {
			lock();
			checkOrCreateTable(clazz);
			cursor = db.rawQuery(proxy.getSql(), proxy.paramsArgs());
			List<T> list = new ArrayList<T>();
			while (cursor.moveToNext()) {
				T t = (T) cursorToBean(cursor, proxy.getRelClass());
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null){
				cursor.close();
				cursor = null;
			}
			unlock();
		}
		return null;
	}
	
	/***
	 * 加载
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T> T  load(Class<T> clazz,Object id){
		EntityInfo entity=EntityInfo.build(clazz);
		if(entity.getIdColumn() == null){
			throw new RuntimeException("主键不可为NULL、必须添加@Column(id=true)");
		}
		return	queryFrist(clazz, entity.getIdColumn().getColumName()+"=?", id);
	}
	
	/**
	 * 查询
	 * @param proxy
	 * @return
	 */
	public <T> T queryFrist(Class<T> clazz, String where, Object... whereargs) {
		if (where.indexOf("limit") < -1) {
			where += " limit 0,1";
		}
		List<T> list=	queryList(clazz, where, whereargs);
		if(list==null||list.size()==0)return null;
		return list.get(0);
	}
	
	public <T> T queryFrist(Class<T> clazz, WhereBuilder where) {
		if(where == null)
			where = WhereBuilder.b();
		if (where.toString().indexOf("limit") < -1) {
			where.expr(" limit 0,1");
		}
		List<T> list=	queryList(clazz, where);
		if(list==null||list.size()==0)return null;
		return list.get(0);
	}

	/**
	 * 通过sql查询
	 * @param clazz
	 * @param sql
	 * @param args
	 * @return
	 */
	public <T> List<T> queryList(Class<T> clazz, String where, Object... whereargs) {
		SqlProxy proxy=SqlProxy.select(clazz, where, whereargs);
		return queryList(clazz,proxy);
	}
	
	public <T> List<T> queryList(Class<T> clazz, WhereBuilder where) {
		SqlProxy proxy=SqlProxy.select(clazz, where);
		return queryList(clazz,proxy);
	}
	
	public <T> List<T> queryAll(Class<T> clazz) {
		SqlProxy proxy=SqlProxy.select(clazz, null);
		return queryList(clazz,proxy);
	}
	
	/**
	 * 对象封装
	 * @param cursor
	 * @param clazz
	 * @return
	 */
	private <T> T cursorToBean(Cursor cursor, Class<T> clazz) {
		EntityInfo entity = EntityInfo.build(clazz);
		if(entity.getIdColumn() == null){
			throw new RuntimeException("主键不可为NULL、必须添加@Column(id=true)");
		}
		
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<ColumnInfo> columnList = entity.getColumnList();
		for(int i=0,size=columnList.size()+1;i<size;i++){
			ColumnInfo column = null;
			column = i == size-1 ? entity.getIdColumn() : columnList.get(i);
			String columnName = column.getColumName();
			Field field = column.getField();
			if(field == null)
				continue;
			if(column.isBean()){
				BeanUtil.setProperty(obj, column,cursor.getString(cursor.getColumnIndex(columnName)));
			}else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
				BeanUtil.setProperty(obj, column,cursor.getInt(cursor.getColumnIndex(columnName)));
			} else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
				BeanUtil.setProperty(obj, column,cursor.getLong(cursor.getColumnIndex(columnName)));
			} else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
				BeanUtil.setProperty(obj, column,cursor.getDouble(cursor.getColumnIndex(columnName)));
			} else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
				BeanUtil.setProperty(obj, column,cursor.getFloat(cursor.getColumnIndex(columnName)));
			} else if (field.getType().equals(String.class)) {
				BeanUtil.setProperty(obj, column,cursor.getString(cursor.getColumnIndex(columnName)));
			} else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
				BeanUtil.setProperty(obj, column, cursor.getInt(cursor.getColumnIndex(columnName)) == 0 ? false : true);
			} else if (field.getType().equals(Date.class)) {
				try {
					BeanUtil.setProperty(obj, column, new Date(cursor.getLong(cursor.getColumnIndex(columnName))));
				} catch (Exception e) { }
			}
		}
		return obj;
	}
	
	
	/**
	 * 获取现有表中字段
	 * @param db
	 * @param tableName
	 * @return
	 */
	private String[] getColumnNames(SQLiteDatabase db, String tableName) {
		String[] columnNames = null;
		Cursor c = null;
		try {
			c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
			if (null != c) {
				int columnIndex = c.getColumnIndex("name");
				if (-1 == columnIndex) {
					return null;
				}
				int index = 0;
				columnNames = new String[c.getCount()];
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					columnNames[index] = c.getString(columnIndex);
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(c != null) c.close();
		}
		return columnNames;
	}
	
	/**
	 * 检测表是否有变化
	 * @param clazz
	 * @param columnNames
	 * @return
	 */
	private boolean changeTable(Class<?> clazz,String [] columnNames){
		EntityInfo entity = EntityInfo.build(clazz);
		List<ColumnInfo> list = entity.getColumnList();
		boolean change = false;//表是否变化
		for(ColumnInfo mColumnInfo : list){
			boolean has = false;
			for(String string : columnNames){
				if(mColumnInfo.getColumName().equals(string)){
					has = true;
					break;
				}
			}
			
			if(!has){
				change = true;
				break;
			}
		}
		return change;
	}
	
	/**
	 * 检查表
	 * @param clazz
	 */
	private void checkOrCreateTable(Class<?> clazz) {
		EntityInfo entity = EntityInfo.build(clazz);
		if (entity.isChecked())
			return;
		
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name='"+entity.getTable()+"' order by name",null);
		Integer count = cursor != null ? cursor.getCount() : null;
		if(cursor != null) 
			cursor.close();
		if(count != null && count > 0){
			String [] columnNames = getColumnNames(db, entity.getTable());
			if(columnNames != null && columnNames.length > 0){
				if(changeTable(clazz, columnNames)){
					String columns = "";
					boolean first = true;
					List<ColumnInfo> list = entity.getColumnList();
					for(int i=0,length=columnNames.length;i<length;i++){
						for(ColumnInfo mColumnInfo : list){
							if(mColumnInfo.getColumName().equals(columnNames[i])){
								columns += (first ? "" : ",")+columnNames[i];
								first = false;
							}
						}
					}
					
					if(TextUtils.isEmpty(columns)){
						//删除
						db.execSQL("DROP TABLE " + entity.getTable());
						//创建表
						String sql = getCreatTableSQL(clazz);
						db.execSQL(sql);
					}else{
						String tempTableName = entity.getTable() + "_temp";
						//表重命名
						db.execSQL("ALTER TABLE " + entity.getTable() + " RENAME TO " + tempTableName);
						//创建表
						String sql = getCreatTableSQL(clazz);
						db.execSQL(sql);
						//旧数据转移
						db.execSQL("INSERT INTO "+entity.getTable()+"("+columns+") SELECT "+columns+" FROM "+tempTableName);
						//删除临时表
						db.execSQL("DROP TABLE "+tempTableName);
					}
				}
			}else{
				//删除
				db.execSQL("DROP TABLE " + entity.getTable());
				//创建表
				String sql = getCreatTableSQL(clazz);
				db.execSQL(sql);
			}
		}else{
			//创建表
			String sql = getCreatTableSQL(clazz);
			db.execSQL(sql);
		}
		entity.setChecked(true);
	}

	public void log(String msg) {
		Log.i(getClass().getSimpleName(), msg);
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
			} catch (IOException e) { }
		} else {
			return SQLiteDatabase.openOrCreateDatabase(dbf, null);
		}
		return null;
	}
	
	private static String getCreatTableSQL(Class<?> clazz){
		EntityInfo entity=EntityInfo.build(clazz);
		if(entity.getIdColumn() == null){
			throw new RuntimeException("主键不可为NULL、必须添加@Column(id=true)");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE IF NOT EXISTS ");
		sql.append(entity.getTable());
		sql.append(" ( ");
		
		//这里添加主键
		IDColumnInfo _id = entity.getIdColumn();
		sql.append(_id.getColumName());
		Class<?> idType = _id.getField().getType(); 
		if( idType== int.class || idType == Integer.class || idType == long.class || idType == Long.class){
			sql.append(" INTEGER");
		}
		sql.append(" PRIMARY KEY");
		if(_id.isAuto()){
			sql.append(" AUTOINCREMENT");
		}
		sql.append(",");
		
		List<ColumnInfo> list = entity.getColumnList();
		for(int i=0,size=list.size();i<size;i++){
			ColumnInfo column = list.get(i);
			Class<?> dataType = column.getField().getType(); 
			
			sql.append(column.getColumName());
			if(column.isBean()){
				sql.append(" TEXT");
			}else if( dataType== int.class || dataType == Integer.class || dataType == long.class || dataType == Long.class){
				sql.append(" INTEGER");
			}else if(dataType == float.class || dataType == Float.class || dataType == double.class || dataType == Double.class){
				sql.append(" REAL");
			}else if (dataType == boolean.class || dataType == Boolean.class) {
				sql.append(" NUMERIC");
			}else{//String
				sql.append(" TEXT");
			}
			
			sql.append(",");
		}
		
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" )");
		return sql.toString();
	}
	
	/**
	 * 删除所有数据表
	 */
	public void dropDb() {
		Cursor cursor = null;
		try{
			lock();
			cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'",null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					db.execSQL("DROP TABLE " + cursor.getString(0));
				}
			}
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			unlock();
		}
	}

	class SqliteDbHelper extends SQLiteOpenHelper {
		public SqliteDbHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) { }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//dropDb();
		}
	}

}
