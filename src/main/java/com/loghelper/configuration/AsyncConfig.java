package com.loghelper.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * 异步配置
 * 配置默认的异步线程池
 */
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private final Executor logHelperAsyncExecutor;

    public AsyncConfig(Executor logHelperAsyncExecutor) {
        this.logHelperAsyncExecutor = logHelperAsyncExecutor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return logHelperAsyncExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
            String errorMessage = String.format("Async method '%s' threw exception: %s", methodName, ex.getMessage());
            throw new RuntimeException(errorMessage, ex);
        };
    }
} 