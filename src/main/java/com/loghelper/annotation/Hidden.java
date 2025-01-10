package com.loghelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cuitianhao
 * 脱敏
 * must use on filed type:String.class
 *
 **/
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hidden {

    /**
     * @return 数据类型
     */
    DataType dataType() default DataType.PHONE;

    /**
     * Specifies the regular expression to use for matching.
     * By default, it matches any non-whitespace character.
     * @return 正则
     */
    String regexp() default "\\S";

    /**
     * 数据类型
     */
    enum DataType {
        /**
         * 手机号13位
         */
        PHONE,
        /**
         * 邮箱
         */
        EMAIL,
        /**
         * 中国身份证
         */
        ID_CARD,
        /**
         * 自定义正则
         */
        REG,
        /**
         * 帐号
         */
        ACCOUNT;
    }
}
