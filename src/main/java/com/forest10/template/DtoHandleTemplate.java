package com.forest10.template;

import com.forest10.bean.ReflectionUtils;
import com.forest10.common.time.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author Forest10
 * @date 2018/9/17 下午3:34
 */
public class DtoHandleTemplate {

	/**
	 * 赋予Dto默认值
	 *
	 * @param entityClass
	 * @return
	 */
	public static void doHandle(Object entityClass) {
		if (entityClass == null) {
			return;
		}
		Class<?> cls = entityClass.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (Objects.isNull(field)) {
				continue;
			}
			field.setAccessible(true);
			try {
				Object value = field.get(entityClass);
				if (Objects.nonNull(value)) {
					continue;
				}
				setDefault(field, entityClass);
			} catch (IllegalAccessException e) {

			}
		}
	}

	private static void setDefault(Field field, Object entityClass) {
		final Class<?> type = field.getType();
		if (type == Integer.class) {
			ReflectionUtils.invokeSetter(entityClass, field.getName(), 0);
		} else if (type == String.class) {
			ReflectionUtils.invokeSetter(entityClass, field.getName(), StringUtils.EMPTY);
		} else if (type == BigDecimal.class) {
			ReflectionUtils.invokeSetter(entityClass, field.getName(), BigDecimal.ZERO);
		} else if (type == Date.class) {
			ReflectionUtils.invokeSetter(entityClass, field.getName(), DateUtil.convertStr2Date("1970-01-01 00:00:00"));
		}
	}


}
