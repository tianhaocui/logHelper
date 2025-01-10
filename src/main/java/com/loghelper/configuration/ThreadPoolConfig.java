package com.loghelper.configuration;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * 支持 TTL 和 @Async
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    @Resource
    private LogHelperProperties properties;

    @Bean("logHelperAsyncExecutor")
    public Executor logHelperAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 使用配置的参数
        executor.setCorePoolSize(properties.getThreadPool().getCorePoolSize());
        executor.setMaxPoolSize(properties.getThreadPool().getMaxPoolSize());
        executor.setQueueCapacity(properties.getThreadPool().getQueueCapacity());
        executor.setThreadNamePrefix(properties.getThreadPool().getThreadNamePrefix());
        // 拒绝策略：调用者运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        
        // 包装为 TTL 线程池
        return TtlExecutors.getTtlExecutor(executor);
    }
} 