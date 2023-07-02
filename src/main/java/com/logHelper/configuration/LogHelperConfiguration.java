package com.logHelper.configuration;

import com.logHelper.aop.PrintLogHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: logHelper
 * @description:
 * @packagename: com.logHelper.configuration
 * @author: cuitianhao
 * @date: 2023/07/01 16:35
 **/
@Configuration
@EnableConfigurationProperties(LogHelperProperties.class)
@EnableAutoConfiguration
@Import({PrintLogHandler.class})
@ComponentScan("com.logHelper.aop")
public class LogHelperConfiguration {

}
