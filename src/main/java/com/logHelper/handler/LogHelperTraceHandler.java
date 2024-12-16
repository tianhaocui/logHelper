package com.logHelper.handler;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Description:
 * Author: cth
 * Created Date: 2024/12/16
 */
public  class  LogHelperTraceHandler {
   private static TransmittableThreadLocal<String> ttl = new TransmittableThreadLocal<>();

    public static String getTraceId() {
        return ttl.get();
    }
    public static void setTraceId(String traceId) {
        ttl.set(traceId);
    }


    public static void remove() {
        ttl.remove();
    }
}
