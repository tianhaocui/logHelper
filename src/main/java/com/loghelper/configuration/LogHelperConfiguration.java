package com.loghelper.configuration;

import com.loghelper.aop.PrintCurlHandler;
import com.loghelper.aop.PrintLogHandler;
import com.loghelper.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * LogHelper 配置
 */
@Configuration
@Import({LogHelperProperties.class, PrintLogHandler.class, PrintCurlHandler.class, SpringContextUtil.class})
public class LogHelperConfiguration {
}
