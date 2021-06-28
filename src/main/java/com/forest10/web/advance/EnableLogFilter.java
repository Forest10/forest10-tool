package com.forest10.web.advance;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:38
 */
@Documented
@Target({ElementType.TYPE})
@Import(LogFilterConf.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableLogFilter {

    String[] excludeUris() default {"/favicon.ico"};

}

