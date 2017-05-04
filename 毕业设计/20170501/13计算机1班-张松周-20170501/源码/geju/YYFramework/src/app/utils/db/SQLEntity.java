package app.utils.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SQLEntity {

	private String table;
	private Map<String,String> field = new HashMap<String, String>();
	
	public void addField(String key,String value){
		field.put(key, value);
	}
	
	public String getTable() {
		return table;
	}
	
	public void setTable(String table) {
		this.table = table;
	}
	/**
	 * 生成建表语句
	 */
	public String getCreateSql() {
		if(field.isEmpty())
			return null;
		StringBuilder s = new StringBuilder();
		s.append("create table "+table+" ( _id integer primary key");
		Set<String> set = field.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = field.get(key);
			s.append(", "+key+" "+value);
		}
		s.append(" )");
		String createSql = s.toString();
		return createSql;
	}
	
	public Map<String, String> getField() {
		return field;
	}

	public SQLEntity(){};
	
	public SQLEntity(String table){
		this.table = table;
	}
	
}
