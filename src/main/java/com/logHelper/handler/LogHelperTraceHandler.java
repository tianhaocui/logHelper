package com.logHelper.handler;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.logHelper.util.TraceIdUtil;

/**
 * Description:
 * Author: cth
 * Created Date: 2024/12/16
 */
public class LogHelperTraceHandler {
    private static TransmittableThreadLocal<String> ttl = new TransmittableThreadLocal<>();

    /**
     * 获取traceId
     *
     * @return traceId
     */
    public static String getTraceId() {
        return ttl.get();
    }

    /**
     * 获取traceId log 字符串
     *
     * @return traceId log
     */
    public static String getTraceLog() {
        return "[logHelper.traceId:" + getTraceId() + "] ";
    }

    /**
     * 设置traceId
     */
    public static void setTraceId() {
        ttl.set(TraceIdUtil.getTraceId());
    }

    /**
     * 移除traceId
     */
    public static void remove() {
        ttl.remove();
    }
}
