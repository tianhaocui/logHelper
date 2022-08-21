package com.loghelper.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@Aspect
public class PrintLogHandler {

    private static final Logger logger = LoggerFactory.getLogger(PrintLogHandler.class);

    @Around("@annotation(com.loghelper.annotation.PrintLog)")
    public Object printLog(ProceedingJoinPoint point) throws Throwable {
        MethodSignature msig = (MethodSignature) point.getSignature();
        Object result = null;
        Object target = point.getTarget();
        try {
            //获取当前方法
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            PrintLog printLog = currentMethod.getAnnotation(PrintLog.class);
            if (printLog != null) {
                printParamLog(printLog, point);
                printResultLog(printLog, point);
            } else {
                result = point.proceed();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 打印参数
     *
     * @param printLog
     * @param point
     */
    private void printParamLog(PrintLog printLog, ProceedingJoinPoint point) {
        if (!printLog.printParameter()) {
            return;
        }
        Object[] args = point.getArgs();
        StringBuilder sb = new StringBuilder();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        getMethodMessage(sb, printLog, methodSignature);
        String[] argsName = methodSignature.getParameterNames();
        //获取不需要打印的参数名
        String[] exception = printLog.exception();
        List<String> exceptionList = null;
        if (exception.length > 0) {
            exceptionList = Arrays.asList(exception);
        }
        for (int i = 0; i < argsName.length; i++) {
            if (exceptionList != null) {
                if (exceptionList.contains(argsName[i])) {
                    continue;
                }
            }
            sb.append(argsName[i]).append(": {}");
            if (i < argsName.length - 1) {
                sb.append(", ");
            }
        }
        printLog(printLog, sb.toString(), args);

    }

    /**
     * 添加方法信息和remark
     *
     * @param sb
     * @param printLog
     * @param methodSignature
     */
    private void getMethodMessage(StringBuilder sb, PrintLog printLog, MethodSignature methodSignature) {
        sb.append("[ ").append(methodSignature.getMethod().getName()).append(" ]    ");
        if (printLog.remark().length() > 0) {
            sb.append("remark.[").append(printLog.remark()).append("]   ");
        }
    }

    /**
     * 执行并打印返回值
     *
     * @param printLog
     * @param point
     */
    private Object printResultLog(PrintLog printLog, ProceedingJoinPoint point) throws Throwable {
        if (!printLog.printResult()) {
            return point.proceed();
        }
        long startTime = System.currentTimeMillis();
        Object proceed = point.proceed();
        StringBuilder sb = new StringBuilder();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        getMethodMessage(sb, printLog, methodSignature);
        sb.append("result: {},").append("use.time: {}ms");
        //耗时
        long cost = System.currentTimeMillis() - startTime;
        printLog(printLog, sb.toString(), proceed, cost);
        return proceed;
    }

    /**
     * 打印日志
     *
     * @param printLog
     * @param logContext
     */
    private void printLog(PrintLog printLog, String logContext, Object... objects) {
        //打印级别
        switch (printLog.level()) {
            case TRACE:
                logger.trace(logContext, objects);
                break;
            case DEBUG:
                logger.debug(logContext, objects);
                break;
            case WARN:
                logger.warn(logContext, objects);
                break;
            case ERROR:
                logger.error(logContext, objects);
                break;
            case INFO:
            default:
                logger.info(logContext, objects);
        }
    }
}
