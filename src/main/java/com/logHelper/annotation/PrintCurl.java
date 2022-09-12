package com.logHelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: logHelper
 * @description:
 * @packagename: com.logHelper.annotation
 * @author: cuitianhao
 * @date: 2022/09/12 18:33
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrintCurl {

    //日志级别
    Level level() default Level.INFO;

    enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
