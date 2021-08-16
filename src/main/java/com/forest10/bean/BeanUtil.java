package com.forest10.bean;

import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Forest10
 * 2017/12/7
 */
public class BeanUtil {



	/**
	 * 合并两个相同的Bean 字段级别.包含处理Map list set
	 * 纯JDK实现.
	 * 只处理desc里面为空(null or StringNull)的值
	 * desc优先级最高
	 *
	 * @param target
	 * @param desc
	 * @param <T>
	 * @throws Exception
	 */
	public static <T> void mergeWithDescHighPriority(T target, T desc) throws Exception {
		BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());

		for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

			final Method descriptorWriteMethod = descriptor.getWriteMethod();
			//如果不能找到写入方法,直接下一个
			if (Objects.isNull(descriptorWriteMethod)) {
				continue;
			}
			final Method descriptorReadMethod = descriptor.getReadMethod();
			//如果不能找到读方法,直接下一个
			if (Objects.isNull(descriptorReadMethod)) {
				continue;
			}
			//获取target现有field的值
			Object targetFieldValue = descriptorReadMethod
					.invoke(target);
			//如果是空或者字符串空,直接下一个
			if (Objects.isNull(targetFieldValue) || StringUtils.isBlank(targetFieldValue.toString())) {
				continue;
			}
			//获取desc现有field的值
			Object desFieldValue = descriptorReadMethod.invoke(
					desc);
			//获取当前field的类型
			final Class<?> propertyType = descriptor.getPropertyType();
			//处理map
			if (Objects.equals(propertyType, Map.class)) {
				//如果现在descMap为空,直接使用targetMap
				if (Objects.isNull(desFieldValue)) {
					descriptorWriteMethod.invoke(desc, targetFieldValue);
					continue;
				}
				Map targetMap = (Map) targetFieldValue;
				Map descMap = (Map) desFieldValue;
				//循环遍历targetMap把descMap里面没有的值塞入
				targetMap.forEach((k, v) -> {
					descMap.putIfAbsent(k, v);
				});
				descriptorWriteMethod.invoke(desc, descMap);
				continue;
			}
			//处理list
			if (Objects.equals(propertyType, List.class)) {
				//如果现在desc为空,直接使用targetCollection
				if (Objects.isNull(desFieldValue)) {
					descriptorWriteMethod.invoke(desc, targetFieldValue);
					continue;
				}
				Collection targetCollection = (Collection) targetFieldValue;
				Collection descCollection = (Collection) desFieldValue;
				//把targetList全部addAll
				descCollection.addAll(targetCollection);
				descriptorWriteMethod.invoke(desc, descCollection.stream().distinct().collect(Collectors.toList()));
				continue;
			}
			//处理Set
			if (Objects.equals(propertyType, Set.class)) {
				//如果现在desc为空,直接使用targetCollection
				if (Objects.isNull(desFieldValue)) {
					descriptorWriteMethod.invoke(desc, targetFieldValue);
					continue;
				}
				Collection targetCollection = (Collection) targetFieldValue;
				Collection descCollection = (Collection) desFieldValue;
				//把targetSet全部addAll
				descCollection.addAll(targetCollection);
				descriptorWriteMethod.invoke(desc, descCollection);
				continue;
			}


			//如果不是基础类型(因为上面已经搞定了基础类型+list+set+map,所以理论上这个是一个Bean).递归调用merge
			if (!propertyType.isPrimitive()) {
				if (Objects.isNull(desFieldValue)) {
					descriptorWriteMethod.invoke(desc, targetFieldValue);
					continue;
				}
				//递归调用
				mergeWithDescHighPriority(targetFieldValue, desFieldValue);
			}

			//如果目标值不为空,就不覆盖了
			if (Objects.nonNull(desFieldValue)) {
				continue;
			}
			//把target的值写入目标bean
			descriptorWriteMethod.invoke(desc, targetFieldValue);

		}
	}
	

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
