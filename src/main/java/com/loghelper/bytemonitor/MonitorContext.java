package com.loghelper.bytemonitor;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

/**
 * 监控上下文
 */
@Slf4j
public class MonitorContext {
    
    private static final TransmittableThreadLocal<Boolean> MONITOR_CONTEXT = 
        new TransmittableThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };

    /**
     * 进入监控上下文
     */
    public static void enter() {
        MONITOR_CONTEXT.set(true);
    }

    /**
     * 退出监控上下文
     */
    public static void exit() {
        MONITOR_CONTEXT.set(false);
    }

    /**
     * 判断是否在监控上下文中
     */
    public static boolean isInContext() {
        return MONITOR_CONTEXT.get();
    }
} 