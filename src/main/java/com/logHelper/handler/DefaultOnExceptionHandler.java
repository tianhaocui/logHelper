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

    /**
     * 异常执行方法
     * @param point 切点
     * @param e 异常
     * @param exception 排除的异常类型
     */
    @Override
    public void onException(ProceedingJoinPoint point, Exception e, String[] exception) {
    }
}
