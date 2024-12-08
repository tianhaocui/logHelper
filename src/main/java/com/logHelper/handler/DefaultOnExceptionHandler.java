package com.logHelper.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-05
 */
@Slf4j
public class DefaultOnExceptionHandler extends OnExceptionHandler{

    @Override
    public void onException(ProceedingJoinPoint point, Exception e) {
        log.error(e.getMessage());
    }
}
