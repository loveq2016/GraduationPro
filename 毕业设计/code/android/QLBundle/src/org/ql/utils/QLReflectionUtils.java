package org.ql.utils;

import java.lang.reflect.InvocationTargetException;

/**
 * A set of helper methods for best-effort method calls via reflection.
 */
/**     
 * 类名称：QLReflectionUtils   
 * 类描述：反射工具
 * 创建人：anan   
 * 创建时间：2012-12-22 下午6:02:19   
 * 修改人：anan  
 * 修改时间：2012-12-22 下午6:02:19   
 * 修改备注：   
 * @version        
 * */
public class QLReflectionUtils {
    public static Object tryInvoke(Object target, String methodName, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        return tryInvoke(target, methodName, argTypes, args);
    }

    public static Object tryInvoke(Object target, String methodName, Class<?>[] argTypes,
            Object... args) {
        try {
            return target.getClass().getMethod(methodName, argTypes).invoke(target, args);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E> E callWithDefault(Object target, String methodName, E defaultValue) {
        try {
            //noinspection unchecked
            return (E) target.getClass().getMethod(methodName, (Class[]) null).invoke(target);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        return defaultValue;
    }
}
