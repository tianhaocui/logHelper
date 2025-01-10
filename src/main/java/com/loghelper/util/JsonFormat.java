package com.loghelper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loghelper.handler.HiddenFieldModule;

/**
 * Description:
 * Author: cth
 * Created Date: 2025/1/10
 */
public class JsonFormat {
    private static final ObjectMapper hiddenMapper = new ObjectMapper();

    static  {
        //todo 支持自定义的序列化方式和自定义模块装载
        hiddenMapper.registerModule(new HiddenFieldModule());
        hiddenMapper.registerModule(new JavaTimeModule());
    }
    public static String format(Object object) {
        try {
            return hiddenMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return String.valueOf(object);
        }
    }
}
