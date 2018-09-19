package forest10.web.annotation;

import forest10.web.conf.LogFilterConf;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:38
 */
@Documented
@Target({ElementType.TYPE})
@Import(LogFilterConf.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableLogFilter {

	String[] excludeUris() default {};

}

