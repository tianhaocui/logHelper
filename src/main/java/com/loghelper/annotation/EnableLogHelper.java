package com.loghelper.annotation;

import com.loghelper.configuration.LogHelperConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(LogHelperConfiguration.class)
public @interface EnableLogHelper {
}
