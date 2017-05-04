package app.utils.db.sqlite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface Column {
	/**
	 * 对应列
	 * @return
	 */
	public String name() default "";
	/**
	 * 是否是主键
	 * @return
	 */
	public boolean id() default false;
	
	/**
	 * 主键是否自增,只对long,int 等数字有效
	 * @return
	 */
	public boolean auto() default true;
	
	/**
	 * Gson处理对象类型Filed
	 * @return
	 */
	public boolean bean() default false;
	
	public boolean NotNull() default false;
}
