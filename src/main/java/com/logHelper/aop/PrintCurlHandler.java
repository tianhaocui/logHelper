package com.logHelper.aop;

import com.logHelper.annotation.PrintCurl;
import com.logHelper.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author cuitianhao
 */
@Component
@Aspect
@Slf4j
public class PrintCurlHandler {
    @Before("@annotation(com.logHelper.annotation.PrintCurl)")
    public void printLog() {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
                printCurlLog(request);
        }
    }

    private void printCurlLog(HttpServletRequest request) {
        String curl = HttpUtils.getCurl(request);
        if (curl != null) {
            log.info(curl);

        }
    }
}

