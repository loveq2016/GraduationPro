package app.utils.sqllite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelp extends SQLiteOpenHelper {

	public SqliteHelp(Context context, String name, int version) {
		super(context, name, null, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table chat_userinfo(id integer primary key autoincrement,username varchar(64),name varchar(64),url varchar(64))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
