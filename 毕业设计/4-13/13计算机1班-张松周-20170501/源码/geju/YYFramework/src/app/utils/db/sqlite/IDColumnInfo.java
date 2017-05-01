package app.utils.db.sqlite;

/**
 * 数据库表主键
 * @author xjm
 */
public class IDColumnInfo extends ColumnInfo{

	protected boolean auto;

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}
	
}
