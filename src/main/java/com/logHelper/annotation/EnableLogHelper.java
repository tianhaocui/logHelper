package com.logHelper.annotation;

import java.lang.annotation.*;

/**
 * 
 * 
 * 
 * @author: cuitianhao
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableLogHelper {
}
