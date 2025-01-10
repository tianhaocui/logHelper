package com.loghelper.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类
 */
@Slf4j
@Component
public class SpringContextUtil implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext applicationContext;
    private static volatile boolean initialized = false;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        log.debug("Setting ApplicationContext");
        SpringContextUtil.applicationContext = context;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        log.debug("ApplicationContext refreshed");
        initialized = true;
    }

    /**
     * 获取 ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        checkInitialized();
        return applicationContext;
    }

    /**
     * 通过名称获取 Bean
     */
    public static Object getBean(String name) {
        checkInitialized();
        return applicationContext.getBean(name);
    }

    /**
     * 通过类型获取 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        checkInitialized();
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过名称和类型获取 Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkInitialized();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 检查是否已初始化
     */
    private static void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException(
                "ApplicationContext has not been initialized yet! " +
                "Please ensure you're not calling this before Spring context is fully started.");
        }
    }
}
