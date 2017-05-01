package app.utils.db.sqlite;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * @author xjm
 */
public class BeanUtil { 
	
	/**
	 * 对象copy只拷贝public的属性
	 * @param from
	 * @param to
	 */
	public static void copyBeanWithOutNull(Object from,Object to){
		Class<?> beanClass = from.getClass();
		Field[] fields=	beanClass.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field=fields[i];
			field.setAccessible(true);
			try {
				Object value=field.get(from);
				if(value!=null){
					field.set(to, value);
				}
			} catch (Exception e) {
			} 
		}
	}
	
	public static Field getDeclaredField(Class<?> clazz,String name){
		try {
			return	clazz.getDeclaredField(name);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 获取属性
	 * @param o
	 * @param info
	 * @return
	 */
	public static Object getProperty(Object o,ColumnInfo info){
		if(info.getField() == null)
			return null;
		try {
			if(info.isBean()){
				Field f = info.getField();
				f.setAccessible(true);
				Object value = f.get(o);
				Gson gson = new GsonBuilder().create();
				return gson.toJson(value,f.getGenericType());
			}else{
				Field f = info.getField();
				f.setAccessible(true);
				return	f.get(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 添加屬性
	 * @param o
	 * @param info
	 * @param value
	 */
	public static void setProperty(Object o,ColumnInfo info,Object value){
		try {
			if(info.isBean()){
				Field f = info.getField();
				Gson gson = new GsonBuilder().create();
				Object obj = gson.fromJson((String) value, f.getGenericType());
				f.setAccessible(true);
				f.set(o, obj);
			}else{
				Field f = info.getField();
				f.setAccessible(true);
				f.set(o, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Method getColumnGetMethod(Class<?> entityType, Field field,String suffix) {
		String fieldName = field.getName();
		Method getMethod = null;
		if (field.getType() == boolean.class) {
			getMethod = getBooleanColumnGetMethod(entityType, fieldName,suffix);
		}
		if (getMethod == null) {
			String methodName = "get" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault()) + fieldName.substring(1)+suffix;
			try {
				getMethod = entityType.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException e) {
				Log.d("T",methodName + " not exist");
			}
		}
		return getMethod;
	}
	
	private static Method getColumnSetMethod(Class<?> entityType, Field field,Class<?> typeClass,String suffix) {
		String fieldName = field.getName();
		Method setMethod = null;
		if (field.getType() == boolean.class) {
			setMethod = getBooleanColumnSetMethod(entityType, field,typeClass,suffix);
		}
		if (setMethod == null) {
			String methodName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault()) + fieldName.substring(1) + suffix;
			try {
				setMethod = entityType.getDeclaredMethod(methodName,typeClass);
			} catch (NoSuchMethodException e) {
				Log.d("T", methodName + " not exist");
			}
		}
		return setMethod;
	}

	private static Method getBooleanColumnGetMethod(Class<?> entityType,final String fieldName,String suffix) {
		String methodName = "is" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault())+ fieldName.substring(1) + suffix;
		if (isStartWithIs(fieldName)) {
			methodName = fieldName + suffix;
		}
		try {
			return entityType.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			Log.d("L",methodName + " not exist");
		}
		return null;
	}
	
	private static Method getBooleanColumnSetMethod(Class<?> entityType, Field field,Class<?> typeClass,String suffix) {
        String fieldName = field.getName();
        String methodName = null;
        if (isStartWithIs(field.getName())) {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase(Locale.getDefault()) + fieldName.substring(3) + suffix;
        } else {
            methodName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault()) + fieldName.substring(1) + suffix;
        }
        try {
            return entityType.getDeclaredMethod(methodName, typeClass);
        } catch (NoSuchMethodException e) {
            Log.d("L",methodName + " not exist");
        }
        return null;
    }
	
    private static boolean isStartWithIs(final String fieldName) {
        return fieldName != null && fieldName.startsWith("is");
    }

}
