package com.logHelper.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logHelper.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cuitianhao
 */
@Component
@Aspect
@Slf4j
public class PrintCurlHandler {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Before("@annotation(com.logHelper.annotation.PrintCurl)")
    public void printLog() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            Object o = requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            try {
                String s = mapper.writeValueAsString(o);
                HttpServletRequest request =mapper.convertValue(s, HttpServletRequest.class);
                printCurlLog(request);

            } catch (JsonProcessingException e) {
            }

        }
    }

    private void printCurlLog(HttpServletRequest request) {
        String curl = HttpUtils.getCurl(request);
        if (curl != null) {
            log.info(curl);
        }
    }
}

