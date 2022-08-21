package com.loghelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: loghelper
 * @description:
 * @packagename: com.loghelper.annotation
 * @author: cil
 * @date: 2022/08/21 16:03
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

    //是否打印返回值
    boolean printResult() default true;

    enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
