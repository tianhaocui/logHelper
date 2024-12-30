package com.logHelper.annotation;

import com.logHelper.exception.NothingException;
import com.logHelper.handler.DefaultOnExceptionHandler;
import com.logHelper.handler.OnExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * 
 * author: cuitianhao
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrintLog {
    /**
     * 异常时不执行onException的类型
     */
    Class<? extends Exception>[] unException() default {};

    /**
     * 异常时候的 参数
     */
    String [] exceptionParam() default {};
    /**
     * 日志级别
     */
    Level level() default Level.INFO;

    /**
     * 备注
     */
    String remark() default "";

    /**
     * 是否打印参数
     */
    boolean printParameter() default true;

    /**
     * 是否打印返回值
     */
    boolean printResult() default true;

    /**
     * 异常时执行
     */
    Class<?extends OnExceptionHandler> onException() default DefaultOnExceptionHandler.class;
    enum Level {
        //trace
        TRACE,
        //debug
        DEBUG,
        //info
        INFO,
        //warn
        WARN,
        //error
        ERROR
    }
    //例外
    Class<? extends Exception>[] exceptException() default {NothingException.class};

}
