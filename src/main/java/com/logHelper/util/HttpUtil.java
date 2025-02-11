package com.loghelper.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * HTTP 工具类
 */
public class HttpUtil {
    private static final String FORMAT_HEADER = "-H \"%1$s:%2$s\"";
    private static final String FORMAT_METHOD = "-X %1$s";
    private static final String FORMAT_BODY = "-d '%1$s'";
    private static final String FORMAT_URL = "\"%1$s\"";
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送 POST 请求
     *
     * @param url  URL
     * @param body 请求体
     * @return 响应内容
     */
    public static String post(String url, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    /**
     * 获取请求体
     *
     * @param request 请求
     * @return 请求体
     */
    public static String getBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            return "";
        }
        return sb.toString();
    }

    /**
     * 获取 curl 命令
     *
     * @param request 请求
     * @return curl 命令
     */
    public static String getCurl(HttpServletRequest request) {
        StringBuilder curl = new StringBuilder("curl ");

        // 添加请求方法
        curl.append(String.format(FORMAT_METHOD, request.getMethod())).append(" ");

        // 添加请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (StringUtils.isNotEmpty(headerValue)) {
                curl.append(String.format(FORMAT_HEADER, headerName, headerValue)).append(" ");
            }
        }

        // 添加请求体
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String body = getBody(request);
            if (StringUtils.isNotEmpty(body)) {
                curl.append(String.format(FORMAT_BODY, body)).append(" ");
            }
        }

        // 添加 URL
        curl.append(String.format(FORMAT_URL, request.getRequestURL().toString()));

        return curl.toString();
    }
}
