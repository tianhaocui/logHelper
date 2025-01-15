package com.loghelper.handler.alert;

import com.loghelper.handler.OnExceptionHandler;
import com.loghelper.util.FeishuAlertUtil;
import com.loghelper.util.JsonFormat;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 飞书告警
 */
public class FeishuAlertHandler extends OnExceptionHandler {

    @Override
    public void onException(ProceedingJoinPoint point, Exception e, Class<? extends Exception>[] exception, String[] exceptionParam, Object o) {
        try {
            String title = point.getSignature().getName();
            String message = JsonFormat.format(point.getArgs());
            FeishuAlertUtil.sendAlert(title, message, e);
        } catch (Exception ex) {
            // 忽略告警发送失败
        }
    }
} 