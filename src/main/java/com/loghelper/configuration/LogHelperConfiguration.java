package com.loghelper.configuration;

import com.loghelper.aop.PerformanceMonitorHandler;
import com.loghelper.aop.PrintCurlHandler;
import com.loghelper.aop.PrintLogHandler;
import com.loghelper.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * LogHelper 配置
 */
@Configuration
@Import({
    PrintLogHandler.class,
    PrintCurlHandler.class,
    SpringContextUtil.class,
    PerformanceMonitorHandler.class,
    ByteMonitorConfiguration.class
})
@PropertySource(
    value = "classpath:loghelper.properties",
    ignoreResourceNotFound = true
)
public class LogHelperConfiguration {
}
