package com.logHelper.handler;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-05
 */
public class DefaultOnExceptionHandler extends OnExceptionHandler{

    @Override
    public void onException(ProceedingJoinPoint point, Exception e) {
        return;
    }
}
