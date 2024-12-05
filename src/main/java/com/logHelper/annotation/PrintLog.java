package com.logHelper.annotation;

import com.logHelper.handler.DefaultOnExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * 
 * @author: cuitianhao
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrintLog {
    String[] exception() default {};

    //日志级别
    Level level() default Level.INFO;

    //备注
    String remark() default "";

    //是否打印参数
    boolean printParameter() default true;

    // 支持返回值打印脱敏
    boolean hiddenResult() default false;
    //是否打印返回值
    boolean printResult() default true;

    Class<DefaultOnExceptionHandler> onException() default DefaultOnExceptionHandler.class;
    enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
