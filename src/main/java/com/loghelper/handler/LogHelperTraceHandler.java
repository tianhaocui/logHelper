package com.loghelper.handler;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.loghelper.util.LogHelperPropertiesUtil;
import com.loghelper.util.TraceIdUtil;

/**
 * 日志追踪处理器
 */
public class LogHelperTraceHandler {
    private static final TransmittableThreadLocal<String> ttl = new TransmittableThreadLocal<>();

    /**
     * 获取 traceId
     */
    public static String getTraceId() {
        return ttl.get();
    }

    /**
     * 获取日志格式的 traceId
     */
    public static String getTraceLog() {
        if (!LogHelperPropertiesUtil.isTraceEnabled()) {
            return "";
        }
        String traceId = getTraceId();
        if (traceId == null) {
            return "[loghelper.traceId:N/A] ";
        }
        return "[loghelper.traceId:" + traceId + "] ";
    }

    /**
     * 设置 traceId
     */
    public static void setTraceId() {
        if (!LogHelperPropertiesUtil.isTraceEnabled()) {
            return;
        }
        if (ttl.get() != null) {
            return;
        }
        ttl.set(TraceIdUtil.getTraceId());
    }

    /**
     * 移除 traceId
     */
    public static void remove() {
        ttl.remove();
    }
}
