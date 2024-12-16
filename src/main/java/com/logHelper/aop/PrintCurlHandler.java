package com.logHelper.aop;

import com.logHelper.handler.LogHelperTraceHandler;
import com.logHelper.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;


/**
 * @author cuitianhao
 */
@Component
@Aspect
@Slf4j
@Order(5)
public class PrintCurlHandler {

    @Before("@annotation(com.logHelper.annotation.PrintCurl)")
    public void printLog() {
        try {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        // get the request
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            printCurlLog(request);
        }
        }catch (Exception e) {
            log.info("printCurl error",e);
        }
    }

    private void printCurlLog(HttpServletRequest request) {
        String curl = HttpUtil.getCurl(request);
        String traceId = LogHelperTraceHandler.getTraceId();

        if (curl != null) {
            log.info("{},{}", traceId, curl);
        }
    }



}

