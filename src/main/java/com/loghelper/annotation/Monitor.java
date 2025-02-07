package com.loghelper.annotation;

import java.lang.annotation.*;

/**
 * 性能监控注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Monitor {
    /**
     * 监控点名称
     */
    String name() default "";

    /**
     * 方法级别的阈值设置（毫秒），-1表示使用全局配置
     */
    long threshold() default -1;

    /**
     * 是否记录参数
     */
    boolean recordParams() default false;

    /**
     * 是否记录返回值
     */
    boolean recordResult() default false;
} 