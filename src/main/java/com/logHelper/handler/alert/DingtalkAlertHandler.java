package com.loghelper.handler.alert;

import com.loghelper.handler.OnExceptionHandler;
import com.loghelper.util.DingtalkAlertUtil;
import com.loghelper.util.JsonFormat;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 钉钉告警
 */
public class DingtalkAlertHandler extends OnExceptionHandler {

    @Override
    public void onException(ProceedingJoinPoint point, Exception e, Class<? extends Exception>[] exception, String[] exceptionParam, Object o) {
        try {
            String title = point.getSignature().getName();
            String message = JsonFormat.format(point.getArgs());
            DingtalkAlertUtil.sendAlert(title, message, e);
        } catch (Exception ex) {
            // 忽略告警发送失败
        }
    }
} 