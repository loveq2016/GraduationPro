package app.utils.db;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ql.utils.debug.QLLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class SQLiteHelper extends SQLiteOpenHelper {
	private final String tag = SQLiteHelper.class.getSimpleName();
	
	private SQLInstance instance;
	private boolean close = false;

	
	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		return super.getWritableDatabase();
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		return super.getReadableDatabase();
	}
	
/**
 * 建数据库
 */
	public SQLiteHelper(Context context,SQLInstance instance){
		super(context,instance.getDatabaseName(),null,instance.getDatabaseVersion());
		this.instance = instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		List<SQLEntity> list = instance.getSQLEntity();
		if (list == null || list.isEmpty())
			return;
		for(SQLEntity entity : list){
			//执行建表操作
			db.execSQL(entity.getCreateSql());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		QLLog.e(tag, "===onUpgrade===");
		QLLog.e(tag, "oldVersion is "+oldVersion);
		QLLog.e(tag, "newVersion is "+newVersion);
		upgradeTables(db);
		instance.onUpdateFinish(db);
		
	}
	
	/**
	 * Upgrade tables. In this method, the sequence is:
	 * <b>
	 * <p>[1] Rename the specified table as a temporary table.
	 * <p>[2] Create a new table which name is the specified name.
	 * <p>[3] Insert data into the new created table, data from the temporary table.
	 * <p>[4] Drop the temporary table.
	 * </b>
	 * @param db The database.
	 * @param tableName The table name.
	 * @param columns The columns range, format is "ColA, ColB, ColC, ... ColN";
	 */
	protected void upgradeTables(SQLiteDatabase db) {
		QLLog.e(tag,"upgradeTables");
		List<SQLEntity> list = instance.getSQLEntity();
		for(SQLEntity entity : list){
			QLLog.e(tag, "table is "+entity.getTable());
			Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name='"+entity.getTable()+"' order by name",null);
			int count = c.getCount();
			if(count > 0){
				String [] columnNames = getColumnNames(db, entity.getTable());
				if(columnNames != null && columnNames.length > 0){
					Map<String,String> field = entity.getField();
					String columns = "";
					boolean first = true;
					for(int i=0,length=columnNames.length;i<length;i++){
						if(field.containsKey(columnNames[i])){
							columns += (first ? "" : ",")+columnNames[i];
							first = false;
						}
					}
					QLLog.e(tag, "columns is "+columns);
					if(TextUtils.isEmpty(columns))
						continue;
					String tableName = entity.getTable();
					String tempTableName = tableName + "_temp";
					//表重命名
					db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tempTableName);
					//创建表
					db.execSQL(entity.getCreateSql());
					//旧数据转移
					db.execSQL("INSERT INTO "+tableName+"("+columns+") SELECT "+columns+" FROM "+tempTableName);
					//删除临时表
					db.execSQL("DROP TABLE "+tempTableName);
				}
			}else{
				db.execSQL(entity.getCreateSql());
			}
		}
	}
	
	
	public static String[] getColumnNames(SQLiteDatabase db, String tableName) {
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

	
	public synchronized boolean isClose(){
		return close;
	}
	
	@Override
	public synchronized void close() {
		super.close();
		close = true;
	}

	public synchronized boolean isEmpty(String table) {
		String sql = "SELECT COUNT() FROM "+table;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(sql, null);
		int count = c.getCount();
		closeDb(db, c);
		return count > 0;
	}
	
	public synchronized boolean isEmpty(String table,String selection,String[] selectionArgs) {
		String sql = "SELECT COUNT() FROM "+table;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(sql, null,selection,selectionArgs,null,null,null);
		int count = c.getCount();
		closeDb(db, c);
		return count > 0;
	}

	/**
	 * 更新数据库
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public synchronized boolean update(String table, ContentValues values, String whereClause, String... whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		final int affectedRows = db.update(table, values, whereClause, whereArgs);
		closeDb(db, null);
		return affectedRows > 0;
	}

	/**
	 * 插入数据库
	 * @param table
	 * @param values
	 * @return
	 */
	public synchronized long insert(String table, ContentValues values) {
		long result = 0L;
		SQLiteDatabase db = getWritableDatabase();
		result = db.insert(table, null, values);
		closeDb(db, null);
		return result;
	}

	/**
	 * 插入或更新
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public synchronized boolean insertOrUpdate(String table, ContentValues values, String whereClause, String... whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(table, null, whereClause, whereArgs, null, null, null);
		int count = c.getCount();
		closeDb(db, c);
		if (count > 0) {
			return update(table, values, whereClause, whereArgs);
		} else {
			return insert(table, values) > 0L;
		}
	}

	/**
	 * 删除
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public synchronized boolean delete(String table, String whereClause, String... whereArgs) {
		int rows = 0;
		SQLiteDatabase db = getWritableDatabase();
		try {
			rows = db.delete(table, whereClause, whereArgs);
		} catch (SQLException e) {
		} catch (Exception e) {
		} finally {
			closeDb(db, null);
		}
		return rows > 0;
	}
	
	public synchronized Cursor query(String table, String whereClause, String... whereArgs){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(table,null,whereClause,whereArgs,null,null,null);
		closeDb(db,null);
		return c;
	}
	
	public synchronized Cursor rawQuery(String sql, String... selectionArgs){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(sql,selectionArgs);
		closeDb(db,null);
		return c;
	}

	
	public synchronized void execSQL(String sql){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql);
		closeDb(db, null);
	}
	
	public synchronized void execSQL(String sql, Object[] bindArgs){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql,bindArgs);
		closeDb(db, null);
	}


	/**
	 * 释放数据库资源
	 **/
	public static void closeDb(SQLiteDatabase db, Cursor c) {
		if (c != null) {
			c.close();
		}
		if (db != null) {
//			db.close();
		}
	}
	
	public static void shutdownDataBase(SQLiteDatabase db){
		if (db != null) {
			db.close();
		}
	}
}
