package forest10.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Forest10
 * @date 2018/5/13 下午5:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SelectAllForbidden {
	//错误提示信息
	String message() default "can`t invoke this method,because SELECT_ALL IS Forbidden!";
}
