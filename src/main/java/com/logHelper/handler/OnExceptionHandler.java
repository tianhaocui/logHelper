package com.logHelper.handler;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Description: 异常处理
 * Author: cth
 * Created Date: 2024-12-05
 */
public abstract class OnExceptionHandler {
    abstract void onException(ProceedingJoinPoint point, Exception e);
}
