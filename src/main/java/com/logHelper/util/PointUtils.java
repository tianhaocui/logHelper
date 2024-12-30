package com.logHelper.util;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-16
 */
public class PointUtils {
    /**
     * 获取方法名
     * @param point 切点
     * @return 方法名
     */
    public static String getMethodName(ProceedingJoinPoint point) {
        Class<?> clazz = point.getTarget().getClass();
        return "[" + clazz.getName() + "." + point.getSignature().getName() + "() ] ";
    }

}
