package com.loghelper.util;

import com.loghelper.configuration.LogHelperProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * LogHelper 配置工具类
 * 用于非 Spring 管理的类获取配置
 */
@Slf4j
public class LogHelperPropertiesUtil {
    private static volatile LogHelperProperties properties;
    private static final Object lock = new Object();

    /**
     * 获取 LogHelper 配置
     */
    public static LogHelperProperties getProperties() {
        LogHelperProperties result = properties;
        if (result == null) {
            synchronized (lock) {
                result = properties;
                if (result == null) {
                    try {
                        result = SpringContextUtil.getBean(LogHelperProperties.class);
                        properties = result;
                    } catch (IllegalStateException e) {
                        log.debug("Spring context not initialized yet, returning default properties");
                        // 返回默认配置
                        result = new LogHelperProperties();
                        // 不缓存默认配置，让其在Spring初始化后能获取真实配置
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取包前缀
     */
    public static String[] getPackagePrefixes() {
        String prefix = getProperties().getAlert().getPackagePrefix();
        return prefix != null ? prefix.split(",") : new String[]{"com.loghelper"};
    }

    /**
     * 是否启用日志追踪
     */
    public static boolean isTraceEnabled() {
        return getProperties().isEnableTrace();
    }

    /**
     * 是否启用参数打印
     */
    public static boolean isParameterPrintEnabled() {
        return getProperties().isEnableParameterPrint();
    }

    /**
     * 获取企业微信 webhook
     */
    public static String getWechatWebhook() {
        return getProperties().getAlert().getWechat().getWebhook();
    }

    /**
     * 获取邮件告警配置
     */
    public static LogHelperProperties.Alert.EmailAlert getEmailAlert() {
        return getProperties().getAlert().getEmail();
    }
} 