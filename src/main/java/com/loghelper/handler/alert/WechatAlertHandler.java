package com.loghelper.handler.alert;

import com.loghelper.handler.OnExceptionHandler;
import com.loghelper.util.WeChatAlertUtil;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 微信告警
 */
public class WechatAlertHandler extends OnExceptionHandler {

    @Override
    public void onException(ProceedingJoinPoint point, Exception e, Class<? extends Exception>[] exception, String[] exceptionParam, Object o) {
        try {
            String title = point.getSignature().getName();
            String message = point.getSignature().getDeclaringTypeName();
            WeChatAlertUtil.sendAlert(title, message, e);
        } catch (Exception ex) {

        }
    }
}