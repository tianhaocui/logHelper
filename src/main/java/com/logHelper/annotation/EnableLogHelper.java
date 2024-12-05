package com.logHelper.annotation;

import com.logHelper.configuration.LogHelperConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: cuitianhao
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(LogHelperConfiguration.class)
public @interface EnableLogHelper {
}
