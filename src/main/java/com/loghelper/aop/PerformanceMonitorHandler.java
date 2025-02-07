package com.loghelper.aop;

import com.loghelper.annotation.Monitor;
import com.loghelper.context.CallChainContext;
import com.loghelper.util.JsonFormat;
import com.loghelper.util.LogHelperPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 性能监控切面
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitorHandler {

    @Around("@annotation(com.loghelper.annotation.Monitor)")
    public Object doMonitor(ProceedingJoinPoint point) throws Throwable {
        // 如果性能监控未启用，直接执行方法
        if (!LogHelperPropertiesUtil.getProperties().getPerformance().isEnabled()) {
            return point.proceed();
        }
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        String name = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        Monitor monitor = signature.getMethod().getAnnotation(Monitor.class);

        // 格式化参数
        String parameters = monitor.recordParams() ? formatParameters(point.getArgs()) : null;

        // 记录调用开始
        CallChainContext.begin( monitor.name().isEmpty() ? name : monitor.name(), parameters);

        try {
            // 执行实际方法
            Object result = point.proceed();

            // 记录调用结束
            String resultStr = monitor.recordResult() ? formatResult(result) : null;
            CallChainContext.end(resultStr, null);

            return result;
        } catch (Throwable e) {
            // 记录异常调用
            CallChainContext.end(null, e);
            throw e;
        }
    }


    private String formatParameters(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        return JsonFormat.format(args);
    }

    private String formatResult(Object result) {
        return JsonFormat.format(result);
    }
} 