package com.loghelper.bytemonitor;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 * 监控转换器
 */
@Slf4j
@Component
public class MonitorTransformer implements BeanPostProcessor {

    private ByteBuddy byteBuddy;

    @PostConstruct
    public void init() {
        ByteBuddyAgent.install();
        byteBuddy = new ByteBuddy();
        log.info("ByteBuddy agent installed successfully");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            
            // 跳过框架类和基础类
            if (shouldSkipClass(targetClass)) {
                return bean;
            }

            log.debug("Processing bean: {} ({})", beanName, targetClass.getName());

            byteBuddy
                .redefine(targetClass)
                .method(ElementMatchers.any()
                    .and(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class)))
                    .and(ElementMatchers.not(ElementMatchers.isStatic()))
                    .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                    .and(ElementMatchers.not(ElementMatchers.isFinal())))
                .intercept(MethodDelegation.to(MethodInterceptor.class))
                .make()
                .load(targetClass.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            log.debug("Successfully enhanced bean: {}", beanName);

        } catch (Exception e) {
            log.error("Failed to enhance bean: " + beanName, e);
        }

        return bean;
    }

    private boolean shouldSkipClass(Class<?> targetClass) {
        String className = targetClass.getName();
        return className.startsWith("org.springframework.") ||
               className.startsWith("java.") ||
               className.startsWith("javax.") ||
               className.startsWith("sun.") ||
               className.startsWith("com.sun.") ||
               className.startsWith("org.hibernate.") ||
               className.startsWith("lombok.");
    }
} 