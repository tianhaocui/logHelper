package com.loghelper.handler.alert;

import com.loghelper.handler.OnExceptionHandler;
import com.loghelper.util.EmailAlertUtil;
import com.loghelper.util.JsonFormat;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 邮件告警
 */
public class EmailAlertHandler extends OnExceptionHandler {

    @Override
    public void onException(ProceedingJoinPoint point, Exception e, Class<? extends Exception>[] exception, String[] exceptionParam, Object o) {
        try {
            String title = point.getSignature().getName();
            String message = JsonFormat.format(point.getArgs());
            EmailAlertUtil.sendAlert(title, message, e);
        } catch (Exception ex) {

        }
    }
}