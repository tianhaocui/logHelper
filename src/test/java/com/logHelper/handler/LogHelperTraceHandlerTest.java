package com.loghelper.handler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogHelperTraceHandlerTest {

    @Test
    public void testTraceIdLifecycle() {
        // 初始状态应该为空
        Assert.assertNull(LogHelperTraceHandler.getTraceId());
        
        // 设置 TraceId
        LogHelperTraceHandler.setTraceId();
        String traceId = LogHelperTraceHandler.getTraceId();
        Assert.assertNotNull(traceId);
        Assert.assertTrue(traceId.length() == 32); // UUID without dashes
        
        // 获取日志格式
        String traceLog = LogHelperTraceHandler.getTraceLog();
        Assert.assertTrue(traceLog.contains(traceId));
        Assert.assertTrue(traceLog.startsWith("[loghelper.traceId:"));
        Assert.assertTrue(traceLog.endsWith("] "));
        
        // 移除 TraceId
        LogHelperTraceHandler.remove();
        Assert.assertNull(LogHelperTraceHandler.getTraceId());
        
        // 测试空 TraceId 的日志格式
        String emptyTraceLog = LogHelperTraceHandler.getTraceLog();
        Assert.assertEquals("[loghelper.traceId:N/A] ", emptyTraceLog);
    }

    @Test
    public void testSetTraceIdIdempotent() {
        LogHelperTraceHandler.setTraceId();
        String firstTraceId = LogHelperTraceHandler.getTraceId();
        
        // 再次设置不应改变现有的 TraceId
        LogHelperTraceHandler.setTraceId();
        String secondTraceId = LogHelperTraceHandler.getTraceId();
        
        Assert.assertEquals(firstTraceId, secondTraceId);
        
        LogHelperTraceHandler.remove();
    }
} 