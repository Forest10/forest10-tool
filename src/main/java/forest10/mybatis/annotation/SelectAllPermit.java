package forest10.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Forest10
 * @date 2018/5/13 下午5:41
 * 应用中大部分场景是绝对不允许查询全部的.此注解支持对需要全部查询的SQL(比如一个公司的总经办职位)不进行拦截
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SelectAllPermit {
	//错误提示信息
	String message() default "can`t invoke this method,because SELECT_ALL IS Forbidden!";
}