package app.utils.db;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public interface SQLInstance {

	public String getDatabaseName();
	
	public int getDatabaseVersion();
	
	public List<SQLEntity> getSQLEntity();
	
	
	public void onUpdateFinish(SQLiteDatabase db);
}
