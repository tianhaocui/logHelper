package com.logHelper.util;

import java.util.UUID;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-08
 */
public class TraceIdUtil {
    /**
     * 获取traceId
     * @return traceId
     */
    public static String getTraceId() {
        return UUID.randomUUID().toString().replace("-", "") ;
    }
}
