package com.logHelper.util;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-16
 */
public class PointUtils {
    public static String getMethodName(ProceedingJoinPoint point) {
        return "[ " + point.getTarget().getClass().getPackage().getName() + point.getTarget().getClass().getName() + "() ]    ";
    }

}
