package com.rchen.xrrpc.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Slf4j
public class ReflectionUtil {
    /**
     * 基本类型转换
     * 例如：Integer.TYPE 与 int.class 相同
     *
     * @param obj
     * @return
     */
    public static Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        if ("java.lang.Integer".equals(typeName)) {
            return Integer.TYPE;
        } else if ("java.lang.Long".equals(typeName)) {
            return Long.TYPE;
        } else if ("java.lang.Float".equals(typeName)) {
            return Float.TYPE;
        } else if ("java.lang.Double".equals(typeName)) {
            return Double.TYPE;
        } else if ("java.lang.Character".equals(typeName)) {
            return Character.TYPE;
        } else if ("java.lang.Boolean".equals(typeName)) {
            return Boolean.TYPE;
        } else if ("java.lang.Short".equals(typeName)) {
            return Short.TYPE;
        } else if ("java.lang.Byte".equals(typeName)) {
            return Byte.TYPE;
        }
        return classType;
    }

    public static Object invokeMethod  (Object serviceBean,
                                        String methodName,
                                        Class<?>[] parameterTypes,
                                        Object[] parameters) throws NoSuchMethodException {
        //TODO cglib 优化Java原生反射的低效率
        Class<?> serviceClass = serviceBean.getClass();
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        try {
            return method.invoke(serviceBean, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
