package com.loghelper.configuration;

import com.loghelper.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LogHelper 自动配置
 */
@Configuration
@EnableConfigurationProperties(LogHelperProperties.class)
public class LogHelperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }
} 