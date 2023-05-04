package com.logHelper.aop;

import com.logHelper.annotation.Hidden;
import com.logHelper.annotation.PrintLog;
import com.logHelper.util.HiddenBeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cuitianhao
 */
@Component
@Aspect
public class PrintLogHandler {
    private static final Logger logger = LogManager.getContext(true).getLogger(PrintLogHandler.class.getName());

    @Around("@annotation(com.logHelper.annotation.PrintLog)")
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
                result = printResultLog(printLog, point);
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
    private void printParamLog(PrintLog printLog, ProceedingJoinPoint point) throws IllegalAccessException {
        if (!printLog.printParameter()) {
            return;
        }
        Object[] args = point.getArgs();
        String packageName = point.getTarget().getClass().getPackage().getName();
        StringBuilder sb = new StringBuilder();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        getMethodMessage(sb, printLog, methodSignature, packageName);

        String[] argsName = methodSignature.getParameterNames();
        //获取不需要打印的参数名
        String[] exception = printLog.exception();
        List<String> exceptionList = null;
        List<Object> argList = new ArrayList<>(Arrays.asList(args));
        if (exception.length > 0) {
            exceptionList = Arrays.asList(exception);
        }
        Method method = methodSignature.getMethod();

        logText(sb, argsName, exceptionList, argList, method);
        printLog(printLog, sb.toString(), argList.toArray());

    }

    private void logText(StringBuilder sb, String[] argsName, List<String> exceptionList, List<Object> argList, Method method) {
        for (int i = 0, j = 0; i < argsName.length; i++, j++) {
            if (exceptionList != null) {
                if (exceptionList.contains(argsName[i])) {
                    argList.remove(j);
                    j--;
                    continue;
                }
            }
            sb.append(argsName[i]).append(": {}");
            Hidden annotation = method.getParameters()[i].getAnnotation(Hidden.class);
            if (annotation != null) {
                if (argList.get(i) != null) {
                    argList.set(i, HiddenBeanUtil.replace(argList.get(i).toString(), annotation.dataType(), annotation.regexp()));
                }
            }

            if (i < argsName.length - 1) {
                sb.append(", ");
            }
        }
    }

    /**
     * 添加方法信息和remark
     *
     * @param sb
     * @param printLog
     * @param methodSignature
     */
    private void getMethodMessage(StringBuilder sb, PrintLog printLog, MethodSignature methodSignature, String packageName) {
        sb.append("[ ").append(packageName).append(".").append(methodSignature.getMethod().getName()).append("() ]    ");
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
        String packageName = point.getTarget().getClass().getPackage().getName();
        getMethodMessage(sb, printLog, methodSignature, packageName);
        sb.append("result: {}   use.time: {}ms");
        //耗时
        long cost = System.currentTimeMillis() - startTime;
        if (proceed != null) {
            Object clone = HiddenBeanUtil.getClone(proceed);
            printLog(printLog, sb.toString(), clone, cost);
            return proceed;

        }
        printLog(printLog, sb.toString(), null, cost);

        return null;
    }

    /**
     * 打印日志
     *
     * @param printLog
     * @param logContext
     */
    private void printLog(PrintLog printLog, String logContext, Object... objects) throws IllegalAccessException {
        Object[] args = new Object[objects.length];
        for (int i = 0; i < objects.length; i++) {
            args[i] = HiddenBeanUtil.getClone(objects[i]);
        }
        //打印级别
        switch (printLog.level()) {
            case TRACE:
                logger.trace(logContext, args);
                break;
            case DEBUG:
                logger.debug(logContext, args);
                break;
            case WARN:
                logger.warn(logContext, args);
                break;
            case ERROR:
                logger.error(logContext, args);
                break;
            case INFO:
            default:
                logger.info(logContext, args);
        }
    }
}
