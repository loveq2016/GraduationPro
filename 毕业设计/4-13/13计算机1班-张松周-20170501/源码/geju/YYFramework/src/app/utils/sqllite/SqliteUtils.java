package app.utils.sqllite;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteUtils {

	private Context context;
	private final String dbname = "database_chat";
	private final int version = 1;
	private SqliteHelp helper = null;

	public SqliteUtils(Context context) {
		this.context = context;
		helper = new SqliteHelp(context, dbname, version);
	}

	public boolean createdb() {
		boolean flag = false;
		helper.getReadableDatabase();
		flag = true;
		return flag;
	}

	public boolean execSql(String sql, Object[] bindArgs) {
		boolean flag = false;
		SQLiteDatabase db = null;

		db = helper.getWritableDatabase();
		db.execSQL(sql, bindArgs);
		flag = true;
		return flag;
	}

	// 查询多条记录
	public List<Map<String, Object>> selectAll(String sql,
			String[] selectionArgs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SQLiteDatabase db = null;
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int columnum = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < columnum; i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(map);
		}
		return list;
	}

	// 查询单条记录
	public Map<String, Object> select(String sql, String[] selectionArgs) {
		Map<String, Object> map = new HashMap<String, Object>();
		SQLiteDatabase db = null;
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int columnum = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			for (int i = 0; i < columnum; i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		return map;
	}

	// 添加记录
	public boolean insert(String tbname, String nullColumnHack,
			ContentValues values) {
		boolean flag = false;
		SQLiteDatabase db = null;
		db = helper.getWritableDatabase();
		long id = db.insert(tbname, nullColumnHack, values);
		if (id > 0) {
			flag = true;
		}
		return flag;
	}

	// 删除记录
	public boolean delete(String tbname, String whereClause, String[] whereArgs) {
		boolean flag = false;
		SQLiteDatabase db = null;
		db = helper.getWritableDatabase();
		int id = db.delete(tbname, whereClause, whereArgs);
		if (id > 0) {
			flag = true;
		}
		return flag;
	}

	// 修改记录
	public boolean update(String tbname, ContentValues values,
			String whereClause, String[] whereArgs) {
		boolean flag = false;
		SQLiteDatabase db = null;
		db = helper.getWritableDatabase();
		int id = db.update(tbname, values, whereClause, whereArgs);
		if (id > 0) {
			flag = true;
		}
		return flag;
	}

	// 查询单条记录
	public Map<String, Object> query(String tbname, String[] columns,
			String selection, String[] selectionArgs) {
		Map<String, Object> map = new HashMap<String, Object>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(tbname, columns, selection, selectionArgs,
				null, null, null);
		int column = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			for (int i = 0; i < column; i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		return map;
	}

	// 查询多条记录
	public List<Map<String, Object>> queryAll(String tbname) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(tbname, null, null, null, null, null, null);
		int column = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < column; i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(map);
		}
		return list;
	}
}