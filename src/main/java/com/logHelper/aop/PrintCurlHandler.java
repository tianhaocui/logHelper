package com.logHelper.aop;

import com.logHelper.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cuitianhao
 */
@Component
@Aspect
@Slf4j
public class PrintCurlHandler {

    @Before("@annotation(com.logHelper.annotation.PrintCurl)")
    public void printLog() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        // get the request
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
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

