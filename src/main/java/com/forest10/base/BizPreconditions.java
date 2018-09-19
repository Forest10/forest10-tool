package com.forest10.base;

import com.forest10.exception.BizException;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Set;

/**
 * 取自guava包中的Preconditions，区别在于抛出自定义异常并做了一些扩展
 *
 * @author Forest10
 * @date 2018/9/12 下午5:12
 */
public class BizPreconditions {

	private BizPreconditions() {
	}


	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 *
	 * @param expression a boolean expression
	 * @throws BizException if {@code expression} is false
	 */
	public static void checkArgument(boolean expression) {
		if (!expression) {
			throw new BizException();
		}
	}

	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 *
	 * @param expression   a boolean expression
	 * @param errorMessage the exception message to use if the check fails; will be converted to a
	 *                     string using {@link String#valueOf(Object)}
	 * @throws BizException if {@code expression} is false
	 */
	public static void checkArgument(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new BizException(String.valueOf(errorMessage));
		}
	}

	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 *
	 * @param expression           a boolean expression
	 * @param errorMessageTemplate a template for the exception message should the check fail. The
	 *                             message is formed by replacing each {@code %s} placeholder in the template with an
	 *                             argument. These are matched by position - the first {@code %s} gets {@code
	 *                             errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the formatted message in
	 *                             square braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
	 *                             are converted to strings using {@link String#valueOf(Object)}.
	 * @throws BizException         if {@code expression} is false
	 * @throws NullPointerException if the check fails and either {@code errorMessageTemplate} or
	 *                              {@code errorMessageArgs} is null (don't let this happen)
	 */
	public static void checkArgument(
			boolean expression,
			String errorMessageTemplate,
			Object... errorMessageArgs) {
		if (!expression) {
			throw new BizException(errorMessageTemplate, errorMessageArgs);
		}
	}

	/**
	 * 检测类型转换
	 *
	 * @param obj   实体实例
	 * @param clazz 要转换到的类型
	 * @param <T>   类型泛型
	 * @return 转换好的实例
	 */
	public static <T> T checkCast(
			Object obj, Class<T> clazz) {
		if (!clazz.isInstance(obj)) {
			throw new BizException("expect %s but is %s", clazz, obj.getClass());
		}
		return clazz.cast(obj);
	}

	/**
	 * 检测类型转换
	 *
	 * @param obj          实体实例
	 * @param clazz        要转换到的类型
	 * @param errorMessage 错误信息
	 * @param <T>          类型泛型
	 * @return 转换好的实例
	 */
	public static <T> T checkCast(
			Object obj, Class<T> clazz, Object errorMessage) {
		if (!clazz.isInstance(obj)) {
			throw new BizException(String.valueOf(errorMessage));
		}
		return clazz.cast(obj);
	}

	/**
	 * 检测类型转换
	 *
	 * @param obj          实体实例
	 * @param clazz        要转换到的类型
	 * @param errorMessage 错误信息
	 * @param <T>          类型泛型
	 * @return 转换好的实例
	 */
	public static <T> T checkCast(
			Object obj, Class<T> clazz, String errorMessage, Object... errorMessageArgs) {
		if (!clazz.isInstance(obj)) {
			throw new BizException(errorMessage, errorMessageArgs);
		}
		return clazz.cast(obj);
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 *
	 * @param reference    an object reference
	 * @param errorMessage the exception message to use if the check fails; will
	 *                     be converted to a string using {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			throw new BizException(String.valueOf(errorMessage));
		}
		return reference;
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 *
	 * @param reference            an object reference
	 * @param errorMessageTemplate a template for the exception message should the
	 *                             check fail. The message is formed by replacing each {@code %s}
	 *                             placeholder in the template with an argument. These are matched by
	 *                             position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
	 *                             Unmatched arguments will be appended to the formatted message in square
	 *                             braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs     the arguments to be substituted into the message
	 *                             template. Arguments are converted to strings using
	 *                             {@link String#valueOf(Object)}.
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference,
	                                 String errorMessageTemplate,
	                                 Object... errorMessageArgs) {
		if (reference == null) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(errorMessageTemplate, errorMessageArgs);
		}
		return reference;
	}

	/**
	 * Ensures that an List reference passed as a parameter to the calling
	 * method is not empty.
	 *
	 * @param reference    an object reference
	 * @param errorMessage the exception message to use if the check fails; will
	 *                     be converted to a string using {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null or empty
	 */
	public static <T> List<T> checkNotEmpty(List<T> reference, Object errorMessage) {
		if (reference == null || Iterables.isEmpty(reference)) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(String.valueOf(errorMessage));
		}
		return reference;
	}

	/**
	 * Ensures that an List reference passed as a parameter to the calling
	 * method is not empty.
	 *
	 * @param reference            an object reference
	 * @param errorMessageTemplate a template for the exception message should the
	 *                             check fail. The message is formed by replacing each {@code %s}
	 *                             placeholder in the template with an argument. These are matched by
	 *                             position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
	 *                             Unmatched arguments will be appended to the formatted message in square
	 *                             braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs     the arguments to be substituted into the message
	 *                             template. Arguments are converted to strings using
	 *                             {@link String#valueOf(Object)}.
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null or empty
	 */
	public static <T> List<T> checkNotEmpty(List<T> reference,
	                                        String errorMessageTemplate,
	                                        Object... errorMessageArgs) {
		if (reference == null || Iterables.isEmpty(reference)) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(errorMessageTemplate, errorMessageArgs);
		}
		return reference;
	}

	/**
	 * Ensures that an Set reference passed as a parameter to the calling
	 * method is not empty.
	 *
	 * @param reference    an object reference
	 * @param errorMessage the exception message to use if the check fails; will
	 *                     be converted to a string using {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null or empty
	 */
	public static <T> Set<T> checkNotEmpty(Set<T> reference, Object errorMessage) {
		if (reference == null || Iterables.isEmpty(reference)) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(String.valueOf(errorMessage));
		}
		return reference;
	}

	/**
	 * Ensures that an Set reference passed as a parameter to the calling
	 * method is not empty.
	 *
	 * @param reference            an object reference
	 * @param errorMessageTemplate a template for the exception message should the
	 *                             check fail. The message is formed by replacing each {@code %s}
	 *                             placeholder in the template with an argument. These are matched by
	 *                             position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
	 *                             Unmatched arguments will be appended to the formatted message in square
	 *                             braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs     the arguments to be substituted into the message
	 *                             template. Arguments are converted to strings using
	 *                             {@link String#valueOf(Object)}.
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null or empty
	 */
	public static <T> Set<T> checkNotEmpty(Set<T> reference,
	                                       String errorMessageTemplate,
	                                       Object... errorMessageArgs) {
		if (reference == null || Iterables.isEmpty(reference)) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(errorMessageTemplate, errorMessageArgs);
		}
		return reference;
	}

	/**
	 * Ensures that an Iterable reference passed as a parameter to the calling
	 * method is not empty.
	 *
	 * @param reference    an object reference
	 * @param errorMessage the exception message to use if the check fails; will
	 *                     be converted to a string using {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null or empty
	 */
	public static <T> Iterable<T> checkNotEmpty(Iterable<T> reference, Object errorMessage) {
		if (reference == null || Iterables.isEmpty(reference)) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(String.valueOf(errorMessage));
		}
		return reference;
	}

	/**
	 * Ensures that an Iterable reference passed as a parameter to the calling
	 * method is not empty.
	 *
	 * @param reference            an object reference
	 * @param errorMessageTemplate a template for the exception message should the
	 *                             check fail. The message is formed by replacing each {@code %s}
	 *                             placeholder in the template with an argument. These are matched by
	 *                             position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
	 *                             Unmatched arguments will be appended to the formatted message in square
	 *                             braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs     the arguments to be substituted into the message
	 *                             template. Arguments are converted to strings using
	 *                             {@link String#valueOf(Object)}.
	 * @return the non-null reference that was validated
	 * @throws BizException if {@code reference} is null or empty
	 */
	public static <T> Iterable<T> checkNotEmpty(Iterable<T> reference,
	                                            String errorMessageTemplate,
	                                            Object... errorMessageArgs) {
		if (reference == null || Iterables.isEmpty(reference)) {
			// If either of these parameters is null, the right thing happens anyway
			throw new BizException(errorMessageTemplate, errorMessageArgs);
		}
		return reference;
	}


}