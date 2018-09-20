package com.forest10.bean;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

/**
 * @author Forest10
 * 2017/12/7
 */
public class BeanUtil {

    /**
     * @param entityClass 类
     * @param fieldName   属性名
     * @return boolean
     */
    private static boolean fieldInEntity(Object entityClass, String fieldName) {
        Class cls = entityClass.getClass();
        final Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
            final String name = field.getName();
            if (StringUtils.equals(fieldName, name)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 拿到 Bean 里面field对应的值
     *
     * @param entityClass 类
     * @param fieldName   属性名
     * @return field对应的值
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getValueByProviedField(Object entityClass, String fieldName)
        throws InvocationTargetException, IllegalAccessException {

        if (!fieldInEntity(entityClass, fieldName)) {
            return null;
        }
        Class cls = entityClass.getClass();
        return Stream.of(cls.getDeclaredMethods())
            .filter(method -> (method.getName()
                .contains("get") && method.getName()
                .substring(3)
                .
                    toLowerCase()
                .equals(fieldName.toLowerCase())))
            .findAny()
            .orElseGet(null)
            .invoke(entityClass);
    }

}
