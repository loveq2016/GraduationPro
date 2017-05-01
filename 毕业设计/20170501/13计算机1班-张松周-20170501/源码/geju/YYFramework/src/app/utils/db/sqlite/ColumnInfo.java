package app.utils.db.sqlite;

import java.lang.reflect.Field;

/**
 * 数据库表字段
 * @author xjm
 */
public class ColumnInfo {
	
	protected Field field;

	protected String columName;
	
	protected boolean NotNull;
	
	protected boolean bean;
	
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getColumName() {
		return columName;
	}

	public void setColumName(String columName) {
		this.columName = columName;
	}

	public boolean isBean() {
		return bean;
	}

	public void setBean(boolean bean) {
		this.bean = bean;
	}

	public boolean isNotNull() {
		return NotNull;
	}

	public void setNotNull(boolean notNull) {
		NotNull = notNull;
	}
}
