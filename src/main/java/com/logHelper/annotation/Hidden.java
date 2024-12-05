package com.logHelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *  脱敏
 * 
 * @author: cuitianhao
 **/
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hidden {

    DataType dataType() default DataType.PHONE;

    // 正则表达式的支持
    String regexp() default "\\S";

    /**
     * 数据类型
     */
    enum DataType {
        PHONE, EMAIL, ID_CARD, REG, ACCOUNT;
    }
}
