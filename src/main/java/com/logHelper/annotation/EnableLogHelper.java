package com.logHelper.annotation;

import com.logHelper.aop.PrintCurlHandler;
import com.logHelper.aop.PrintLogHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @program: logHelper
 * @description:
 * @packagename: com.logHelper.annotation
 * @author: cuitianhao
 * @date: 2022/09/04 21:01
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableLogHelper {
}
