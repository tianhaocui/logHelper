package com.logHelper.annotation;

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

    DataType dataType() default DataType.PHONE;

    /**
     * Specifies the regular expression to use for matching.
     * By default, it matches any non-whitespace character.
     *
     * @return the regular expression
     */
    String regexp() default "\\S";

    /**
     * 数据类型
     */
    enum DataType {
        PHONE, EMAIL, ID_CARD, REG, ACCOUNT;
    }
}
