package com.loghelper.bytemonitor;

import com.loghelper.annotation.Monitor;
import com.loghelper.context.CallChainContext;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 方法拦截器
 */
@Slf4j
public class MethodInterceptor {

    @RuntimeType
    public static Object intercept(@This Object target,
                                 @Origin Method method,
                                 @AllArguments Object[] args,
                                 @SuperCall Callable<?> callable) throws Exception {

        String signature = target.getClass().getSimpleName() + "." + method.getName();
        Monitor monitor = method.getAnnotation(Monitor.class);
        boolean isMonitorMethod = monitor != null;
        boolean wasInContext = MonitorContext.isInContext();

        log.debug("Intercepting method: {}, hasMonitor: {}, wasInContext: {}",
                 signature, isMonitorMethod, wasInContext);

        // 如果是监控方法或已在监控上下文中，则记录调用
        boolean shouldRecord = isMonitorMethod || wasInContext;
        long startTime = 0;

        if (shouldRecord) {
            if (isMonitorMethod && !wasInContext) {
                log.debug("Starting new monitor context for method: {}", signature);
                MonitorContext.enter();
                CallChainContext.initRootContext();
            }

            // 安全获取注解配置
            boolean recordParams = monitor != null && monitor.recordParams();
            String params = recordParams ? formatParams(args) : null;

            startTime = System.nanoTime();
            CallChainContext.begin(signature, params);
            log.debug("Started recording method: {}", signature);
        }

        try {
            Object result = callable.call();

            if (shouldRecord) {
                long duration = System.nanoTime() - startTime;
                boolean recordResult = monitor != null && monitor.recordResult();
                String resultStr = recordResult ? formatResult(result) : null;

                CallChainContext.end(resultStr, null);
                CallChainContext.recordDuration(duration);
                log.debug("Completed recording method: {} in {} ms", signature, duration);
            }

            return result;
        } catch (Exception e) {
            if (shouldRecord) {
                CallChainContext.end(null, e);
                log.debug("Failed recording method: {} with error: {}",
                         signature, e.getMessage());
            }
            throw e;
        } finally {
            if (isMonitorMethod && !wasInContext) {
                MonitorContext.exit();
                log.debug("Exited monitor context for method: {}", signature);
            }
        }
    }

    private static String formatParams(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(arg);
        }
        return sb.toString();
    }

    private static String formatResult(Object result) {
        return result == null ? "null" : result.toString();
    }
} 