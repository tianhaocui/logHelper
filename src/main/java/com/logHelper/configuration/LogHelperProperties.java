package com.logHelper.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  一些全局配置
 **/

@ConfigurationProperties(prefix = "loghelper")
public class LogHelperProperties {
}
