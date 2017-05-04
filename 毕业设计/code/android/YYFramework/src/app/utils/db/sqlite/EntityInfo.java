package app.utils.db.sqlite;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

/**
 * 数据库表结构实体
 * @author xjm
 *
 */
public class EntityInfo {

	private String table;
	
	private IDColumnInfo idColumn;
	
	private final List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
	
	private boolean checked=false;
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	private static final Map<Object,EntityInfo> entitys=new HashMap<Object,EntityInfo>();
	
	/**
	 * @NoColumn、static、final标志忽略
	 * @param field
	 * @return
	 */
	private boolean isContinue(Field field){
		return field.getAnnotation(NoColumn.class) != null || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
	}
	
	/**
	 * 是否可以使用@Column(as=true)
	 * @param field
	 * @return
	 */
	private boolean isBean(Field field){
		Class<?> type = field.getType();
		boolean bool = 
			type.equals(int.class) ||
			type.equals(Integer.class) ||
			type.equals(long.class) ||
			type.equals(Long.class) ||
			type.equals(float.class) ||
			type.equals(Float.class) ||
			type.equals(double.class) ||
			type.equals(Double.class) ||
			type.equals(boolean.class) ||
			type.equals(Boolean.class) ||
			type.equals(String.class) ||
			type.equals(Date.class) ||
			type.equals(java.sql.Date.class);
			
		return !bool; 
	}
	
	private EntityInfo(Class<?> entityClazz){
		DbTable mTable=entityClazz.getAnnotation(DbTable.class);
		if(mTable != null){
			table = TextUtils.isEmpty(mTable.name()) ? entityClazz.getSimpleName() : mTable.name();
		}else{
			table = entityClazz.getName().replace('.', '_');
		}
		
		Field[] fields=entityClazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field=fields[i];
			if(isContinue(field))
				continue;
			Column column = field.getAnnotation(Column.class);
			String columName;
			if(column != null){
				columName=TextUtils.isEmpty(column.name()) ? field.getName() : column.name(); 
				if(column.id()){
					idColumn = new IDColumnInfo();
					idColumn.setField(field);
					idColumn.setColumName(columName);
					Class<?> primaryClazz=field.getType();
					if( primaryClazz == int.class || primaryClazz==Integer.class || primaryClazz == long.class || primaryClazz == Long.class){
						idColumn.setAuto(column.auto());
					}else{
						throw new RuntimeException("主键只能是int、Integer、long、Long");
					}
					//主键不添加到List<ColumnInfo>中
					continue;
				}
			}else{
				columName = field.getName();
			}
			
			if(!isBean(field) && column != null && column.bean()){
				throw new RuntimeException("field : "+field.getName()+" --- int、Integer、long、Long、float、Float、double、Double、Boolean、String、Date不需要@Column(bean=true)");
			}else if(isBean(field) && (column == null || !column.bean())){
				String string = "int、Integer、long、Long、float、Float、double、Double、Boolean、String、Date以外字段\n";
				string += "field :"+field.getName()+" 必须添加@Column(bean=true)";
				throw new RuntimeException(string);
			}
			
			ColumnInfo mColumnInfo = new ColumnInfo();
			mColumnInfo.setField(field);
			mColumnInfo.setColumName(columName);
			if(column != null)
				mColumnInfo.setNotNull(column.NotNull());
			if(column != null)
				mColumnInfo.setBean(column.bean());
			columnList.add(mColumnInfo);
		}
		
		if(idColumn == null){
			throw new RuntimeException("主键不可为NULL、必须添加@Column(id=true)");
		}
	}
	
	public static EntityInfo build(Class<?> entityClazz){
		EntityInfo info=entitys.get(entityClazz);
		if(info==null){
			info=new EntityInfo(entityClazz);
			entitys.put(entityClazz, info);
		}
		return info;
	}
	
	public String getTable() {
		return table;
	}
	
	public IDColumnInfo getIdColumn() {
		return idColumn;
	}

	public List<ColumnInfo> getColumnList(){
		return columnList;
	}
}
