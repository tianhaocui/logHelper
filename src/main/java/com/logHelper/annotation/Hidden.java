package com.logHelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: loghelper
 * @description: 脱敏
 * @packagename: com.logHelper.annotation
 * @author: wulingren
 * @date: 2022/08/27 15:26
 **/
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hidden {

    DataType dataType() default DataType.OTHER;
    //todo 正则表达式的支持

    enum DataType{
        PHONE,EMAIL,ID_CARD,OTHER
    }
}
