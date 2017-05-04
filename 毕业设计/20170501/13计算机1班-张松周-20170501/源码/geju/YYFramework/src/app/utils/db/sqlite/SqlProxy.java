package app.utils.db.sqlite;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.text.TextUtils;


/**
 * sql组装代理类
 */
public class SqlProxy {
	
	StringBuffer sql;
	List<Object> params;
	Class<?> clazz;
	
	public Class<?> getRelClass(){
		return clazz;
	}
	
	private SqlProxy() {
		sql=new StringBuffer();
		params=new ArrayList<Object>();
	}
	
	/**
	 * 插入数据
	 * @param obj
	 * @return
	 */
	@Deprecated
	public static SqlProxy save(Object obj){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(obj.getClass());
		proxy.sql.append("INSERT INTO ").append(entity.getTable()).append("(");
		StringBuffer p=new StringBuffer("(");
		
		List<ColumnInfo> list = entity.getColumnList();
		for(int i=0,size=list.size();i<size;i++){
			ColumnInfo column = list.get(i);
			proxy.params.add(BeanUtil.getProperty(obj, column));
			proxy.sql.append(column.getColumName());
			if(i != size-1)
				proxy.sql.append(" ,");
			p.append("?");
			if(i != size-1)
				p.append(" ,");
		}
		
		proxy.sql.append(")");
		p.append(")");
		proxy.sql.append(" VALUES ").append(p);
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	
	/**
	 * 插入数据
	 * @param obj
	 * @param values
	 * @return
	 */
	public static ContentValues insert(Object obj){
		EntityInfo entity=EntityInfo.build(obj.getClass());
		ContentValues cv = new ContentValues();
		List<ColumnInfo> list = entity.getColumnList();
		for(int i=0,size=list.size();i<size;i++){
			ColumnInfo column = list.get(i);
			Object value = BeanUtil.getProperty(obj, column);
			if(value != null){
				cv.put(column.getColumName(), obj2String(value));
			}
		}
		return cv;
	}
	
	/**
	 * 更新数据
	 * @param obj
	 * @return
	 */
	public static SqlProxy update(Object obj){
		EntityInfo entity=EntityInfo.build(obj.getClass());
		
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("UPDATE ").append(entity.getTable()).append(" SET ");
		
		List<ColumnInfo> list = entity.getColumnList();
		for(int i=0,size=list.size();i<size;i++){
			ColumnInfo column = list.get(i);
			Object value = BeanUtil.getProperty(obj, column);
			if(!(value == null && column.isNotNull())){
				proxy.sql.append(column.getColumName()).append("=? ,");
				proxy.params.add(value);
			}
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		proxy.sql.append(" WHERE ")
		.append(entity.getIdColumn().getColumName())
		.append("=?");
		proxy.params.add(BeanUtil.getProperty(obj,entity.getIdColumn()));
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	/**
	 * 根据条件更新数据
	 * @param obj
	 * @param where
	 * @return
	 */
	public static SqlProxy update(Object obj,String where,Object... whereargs){
		EntityInfo entity=EntityInfo.build(obj.getClass());
		
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("UPDATE ").append(entity.getTable()).append(" SET ");
		
		List<ColumnInfo> list = entity.getColumnList();
		for(int i=0,size=list.size();i<size;i++){
			ColumnInfo column = list.get(i);
			Object value = BeanUtil.getProperty(obj, column);
			if(!(value == null && column.isNotNull())){
				proxy.sql.append(column.getColumName()).append("=? ,");
				proxy.params.add(value);
			}
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		proxy.clazz=obj.getClass();
		proxy.buildWhere(where, whereargs);
		return proxy;
	}
	
	/**
	 * 根据条件更新数据
	 * @param obj
	 * @param where
	 * @return
	 */
	public static SqlProxy update(Object obj,WhereBuilder where){
		EntityInfo entity=EntityInfo.build(obj.getClass());
		
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("UPDATE ").append(entity.getTable()).append(" SET ");
		
		List<ColumnInfo> list = entity.getColumnList();
		for(int i=0,size=list.size();i<size;i++){
			ColumnInfo column = list.get(i);
			Object value = BeanUtil.getProperty(obj, column);
			if(!(value == null && column.isNotNull())){
				proxy.sql.append(column.getColumName()).append("=? ,");
				proxy.params.add(value);
			}
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		if(where != null)
			proxy.sql.append(where.toString());
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	/**
	 * 更新条件更新部分数据
	 * @param values
	 * @param where
	 * @return
	 */
	public static SqlProxy update(Class<?> clazz,Map<String,Object> values,String where,Object... whereargs){
		EntityInfo entity=EntityInfo.build(clazz);
		
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("UPDATE ").append(entity.getTable()).append(" SET ");
		
		int i = 0;
		int size = values.size();
		Set<String> keys = values.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Object value = values.get(key);
			
			proxy.sql.append(key).append("=?");
			if(i != size-1)
				proxy.sql.append(" ,");
			proxy.params.add(value);
			i++;
		}
		
		proxy.buildWhere(where, whereargs);
		proxy.clazz=clazz;
		return proxy;
	}
	
	/**
	 * 更新条件更新部分数据
	 * @param values
	 * @param where
	 * @return
	 */
	public static SqlProxy update(Class<?> clazz,Map<String,Object> values,WhereBuilder where){
		EntityInfo entity=EntityInfo.build(clazz);
		
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("UPDATE ").append(entity.getTable()).append(" SET ");
		
		int i = 0;
		int size = values.size();
		Set<String> keys = values.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Object value = values.get(key);
			
			proxy.sql.append(key).append("=?");
			if(i != size-1)
				proxy.sql.append(" ,");
			proxy.params.add(value);
			i++;
		}
		
		if(where != null)
			proxy.sql.append(where.toString());
		proxy.clazz=clazz;
		return proxy;
	}
	
	
	/**
	 * 删除数据
	 * @param obj
	 * @return
	 */
	public static SqlProxy deleteAll(Class<?> clazz){
		EntityInfo entity=EntityInfo.build(clazz);
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("DELETE FROM ")
			.append(entity.getTable());
		proxy.clazz=clazz;
		return proxy;
	}
	
	/**
	 * 删除数据
	 * @param obj
	 * @return
	 */
	public static SqlProxy delete(Object obj){
		EntityInfo entity=EntityInfo.build(obj.getClass());
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("DELETE FROM ")
		.append(entity.getTable())
		.append(" WHERE ")
		.append(entity.getIdColumn().getColumName())
		.append("=?");
		proxy.params.add(BeanUtil.getProperty(obj, entity.getIdColumn()));
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	/**
	 * 根据条件删除数据
	 * @param where
	 * @return
	 */
	public static SqlProxy delete(Class<?> clazz,String where,Object... whereargs){
		EntityInfo entity=EntityInfo.build(clazz);
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("DELETE FROM ").append(entity.getTable());
		proxy.buildWhere(where, whereargs);
		proxy.clazz=clazz;
		return proxy;
	}
	
	/**
	 * 根据条件删除数据
	 * @param where
	 * @return
	 */
	public static SqlProxy delete(Class<?> clazz,WhereBuilder where){
		EntityInfo entity=EntityInfo.build(clazz);
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("DELETE FROM ").append(entity.getTable());
		if(where != null)
			proxy.sql.append(where.toString());
		proxy.clazz=clazz;
		return proxy;
	}
	
	/**
	 * 删除数据
	 * @param clazz
	 * @param pkvalue
	 * @return
	 */
	public static SqlProxy delete(Class<?> clazz,Object pkvalue){
		EntityInfo entity=EntityInfo.build(clazz);
		SqlProxy	proxy=new SqlProxy();
		proxy.sql.append("DELETE FROM ")
			.append(entity.getTable())
			.append(" WHERE ")
			.append(entity.getIdColumn().getColumName())
			.append("=?");
		proxy.params.add(pkvalue);
		proxy.clazz=clazz;
		return proxy;
	}
	
	public static SqlProxy select(Class<?> clazz,String where,Object... whereargs){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(clazz);
		proxy.sql.append("SELECT * FROM ").append(entity.getTable());
		proxy.clazz=clazz;
		proxy.buildWhere(where, whereargs);
		return proxy;
	}
	
	public static SqlProxy select(Class<?> clazz,WhereBuilder where){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(clazz);
		proxy.sql.append("SELECT * FROM ").append(entity.getTable());
		proxy.clazz=clazz;
		if(where != null)
			proxy.sql.append(where.toString());
		return proxy;
	}
	
	private void buildWhere(String where,Object[] whereargs){
		if(TextUtils.isEmpty(where))
			return;
		sql.append(" WHERE ");
		sql.append(where);
		if(whereargs != null){
			for (int i = 0; i < whereargs.length; i++) {
				params.add(whereargs[i]);
			}
		}
	}
	
	public String getSql(){
		return sql.toString();
	}
	
	public String[] paramsArgs(){
		String[] args=new String[params.size()];
		for (int i = 0; i < args.length; i++) {
			Object obj=params.get(i);
			if(obj == null)
				continue;
			args[i] = obj2String(obj);
		}
		return args;
	}
	
	private static String obj2String(Object obj){
		if(obj == null)
			return "";
		if(obj.getClass().equals(Date.class) || obj instanceof Date){
			Date date=(Date) obj;
			return date.getTime()+"";
		}else if(obj instanceof Boolean){
			Boolean bool = (Boolean) obj;
			return bool ? "1" : "0";
		}else{
			return obj.toString();
		}
	}
	
}
