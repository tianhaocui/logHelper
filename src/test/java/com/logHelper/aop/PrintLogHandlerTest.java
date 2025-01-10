package com.loghelper.aop;

import com.loghelper.annotation.PrintLog;
import com.loghelper.handler.LogHelperTraceHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PrintLogHandlerTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private PrintLogHandler printLogHandler;

    @Before
    public void setup() {
        printLogHandler = new PrintLogHandler();
        when(joinPoint.getSignature()).thenReturn(methodSignature);
    }

    @Test
    public void testPrintLogNormal() throws Throwable {
        // 准备测试数据
        TestService testService = new TestService();
        Method method = TestService.class.getMethod("testMethod", String.class);
        
        when(joinPoint.getTarget()).thenReturn(testService);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test"});
        when(joinPoint.proceed()).thenReturn("result");
        
        // 执行测试
        Object result = printLogHandler.printLog(joinPoint);
        
        // 验证结果
        verify(joinPoint, times(1)).proceed();
        assert result.equals("result");
        
        // 确保 TraceId 被正确清理
        assert LogHelperTraceHandler.getTraceId() == null;
    }

    @Test
    public void testPrintLogWithException() throws Throwable {
        // 准备测试数据
        TestService testService = new TestService();
        Method method = TestService.class.getMethod("testMethod", String.class);
        
        when(joinPoint.getTarget()).thenReturn(testService);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("test exception"));
        
        try {
            printLogHandler.printLog(joinPoint);
        } catch (Throwable t) {
            assert t instanceof RuntimeException;
            assert t.getMessage().equals("test exception");
        }
        
        // 确保 TraceId 被正确清理
        assert LogHelperTraceHandler.getTraceId() == null;
    }

    // 测试用的服务类
    private static class TestService {
        @PrintLog
        public String testMethod(String param) {
            return "result";
        }
    }
} 