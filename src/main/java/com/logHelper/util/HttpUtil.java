package com.logHelper.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FastByteArrayOutputStream;

import jakarta.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class HttpUtil {
    private static final String FORMAT_HEADER = "-H \"%1$s:%2$s\"";
    private static final String FORMAT_METHOD = "-X %1$s";
    private static final String FORMAT_BODY = "-d '%1$s'";
    private static final String FORMAT_URL = "\"%1$s\"";
    private static final String CONTENT_TYPE = "Content-Type";

    public static String getCurl(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        parts.add("curl");
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String contentType = request.getContentType();
        String queryString = request.getQueryString();
        parts.add(String.format(FORMAT_METHOD, method.toUpperCase()));

        Map<String, String> headers = new HashMap<>(16);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headers.put(key, request.getHeader(key));
        }
        headers.forEach((k, v) -> parts.add(String.format(FORMAT_HEADER, k, v)));
        if (StringUtils.isNotEmpty(contentType) && !headers.containsKey(CONTENT_TYPE)) {
            parts.add(String.format(FORMAT_HEADER, CONTENT_TYPE, contentType));
        }
        if (StringUtils.isNotEmpty(queryString)) {
            url = url+ "?"+queryString;
        }
        String body = null;
        try {
            FastByteArrayOutputStream fastByteArrayOutputStream = readUtf8(request.getInputStream());
            body = new String(fastByteArrayOutputStream.toByteArray());
        } catch (IOException ignore) {
        }
        if (StringUtils.isNotEmpty(body)) {
            parts.add(String.format(FORMAT_BODY, body));
        }
        parts.add(String.format(FORMAT_URL, url));
        return StringUtils.join(parts," " );
    }

    private static FastByteArrayOutputStream readUtf8(InputStream in) throws IOException {
        FastByteArrayOutputStream out;
        if (in instanceof FileInputStream) {
            out = new FastByteArrayOutputStream(in.available());
        } else {
            out = new FastByteArrayOutputStream();
        }
        return out;
    }


}
