package com.loghelper.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogUtilTest {

    @Test
    public void testIsTraceEnabled() {
        boolean result = LogUtil.isTraceEnabled();
        Assert.assertFalse(result);
    }

    @Test
    public void testTrace() {
        // 测试不同重载方法
        LogUtil.trace("test message");
        LogUtil.trace("test format {}", "arg");
        LogUtil.trace("test format {} {}", "arg1", "arg2");
        LogUtil.trace("test format {} {} {}", "arg1", "arg2", "arg3");
        LogUtil.trace("test message", new RuntimeException("test"));
    }

    @Test
    public void testIsDebugEnabled() {
        boolean result = LogUtil.isDebugEnabled();
        Assert.assertFalse(result);
    }

    @Test
    public void testDebug() {
        LogUtil.debug("test message");
        LogUtil.debug("test format {}", "arg");
        LogUtil.debug("test format {} {}", "arg1", "arg2");
        LogUtil.debug("test format {} {} {}", "arg1", "arg2", "arg3");
        LogUtil.debug("test message", new RuntimeException("test"));
    }

    @Test
    public void testIsInfoEnabled() {
        boolean result = LogUtil.isInfoEnabled();
        Assert.assertTrue(result);
    }

    @Test
    public void testInfo() {
        LogUtil.info("test message");
        LogUtil.info("test format {}", "arg");
        LogUtil.info("test format {} {}", "arg1", "arg2");
        LogUtil.info("test format {} {} {}", "arg1", "arg2", "arg3");
        LogUtil.info("test message", new RuntimeException("test"));
    }

    @Test
    public void testIsWarnEnabled() {
        boolean result = LogUtil.isWarnEnabled();
        Assert.assertTrue(result);
    }

    @Test
    public void testWarn() {
        LogUtil.warn("test message");
        LogUtil.warn("test format {}", "arg");
        LogUtil.warn("test format {} {}", "arg1", "arg2");
        LogUtil.warn("test format {} {} {}", "arg1", "arg2", "arg3");
        LogUtil.warn("test message", new RuntimeException("test"));
    }

    @Test
    public void testIsErrorEnabled() {
        boolean result = LogUtil.isErrorEnabled();
        Assert.assertTrue(result);
    }

    @Test
    public void testError() {
        LogUtil.error("test message");
        LogUtil.error("test format {}", "arg");
        LogUtil.error("test format {} {}", "arg1", "arg2");
        LogUtil.error("test format {} {} {}", "arg1", "arg2", "arg3");
        LogUtil.error("test message", new RuntimeException("test"));
    }
} 