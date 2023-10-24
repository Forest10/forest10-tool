package com.forest10.template.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Forest10
 * @date 2023/10/24 12:01
 */
@Getter
@AllArgsConstructor
public enum ServiceFallBackEnum {
    //不处理,直接抛异常
    NONE,
    //异常处理,不抛出
    HANDLE_EXCEPTION,
    //异常处理之后抛出
    HANDLE_EXCEPTION_AND_THROW,
    //服务降级默认处理
    EXEC_FALLBACK,

    ;

}
