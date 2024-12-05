package com.logHelper.configuration;

import com.logHelper.aop.PrintCurlHandler;
import com.logHelper.aop.PrintLogHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * 
 * 
 * @author: cuitianhao
 **/
@Configuration
@EnableConfigurationProperties(LogHelperProperties.class)
@EnableAutoConfiguration
@Import({PrintLogHandler.class, PrintCurlHandler.class})
//@ComponentScan("com.logHelper.aop")
public class LogHelperConfiguration {

}
