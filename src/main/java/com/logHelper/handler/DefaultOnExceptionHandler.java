package com.logHelper.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-05
 */
@Slf4j
public class DefaultOnExceptionHandler extends OnExceptionHandler {

    public static final DefaultOnExceptionHandler INSTANCE = new DefaultOnExceptionHandler();

    @Override
    public void onException(ProceedingJoinPoint point, Exception e, String[] exception) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String packageName = point.getTarget().getClass().getPackage().getName();
        String name = methodSignature.getMethod().getName();
        log.error("exception:{},{}",packageName,name,e);
    }
}
