package com.logHelper.aop;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logHelper.annotation.PrintLog;
import com.logHelper.handler.HiddenFieldModule;
import com.logHelper.handler.LogHelperTraceHandler;
import com.logHelper.handler.OnExceptionHandler;
import com.logHelper.util.HiddenBeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author cuitianhao
 */
@Component
@Aspect
public class PrintLogHandler {
    private static final Logger logger = LogManager.getContext(true).getLogger(PrintLogHandler.class.getName());
    private final ObjectMapper hiddenMapper = new ObjectMapper();

    {
        //todo 支持自定义的序列化方式和自定义模块装载
        hiddenMapper.registerModule(new HiddenFieldModule());
        hiddenMapper.registerModule(new JavaTimeModule());

    }


    /**
     * 从切面打印 log
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.logHelper.annotation.PrintLog)")
    public Object printLog(ProceedingJoinPoint point) throws Throwable {
        String traceId = getTraceId();
        LogHelperTraceHandler.setTraceId(traceId);
        MethodSignature msig = (MethodSignature) point.getSignature();
        Object result = null;
        Object target = point.getTarget();
        try {
            //获取当前方法
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            PrintLog printLog = currentMethod.getAnnotation(PrintLog.class);
            //打印参数日志
            printParamLog(traceId,printLog, point);
            result = printResultLog(traceId,printLog, point);
        } catch (NoSuchMethodException e) {
            logger.debug("printLog exception on ", e);
        }finally {
            LogHelperTraceHandler.remove();
        }
        return result;
    }

    /**
     * 打印参数
     * @param traceId 链路id
     * @param printLog 需要打印的log
     * @param point    切点
     */
    private void printParamLog(String traceId,PrintLog printLog, ProceedingJoinPoint point) {
        if (!printLog.printParameter()) {
            return;
        }
        Object[] args = point.getArgs();
        String packageName = point.getTarget().getClass().getPackage().getName() + point.getTarget().getClass().getName();
        StringBuilder sb = new StringBuilder().append(traceId);
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        getMethodMessage(sb, printLog, methodSignature, packageName);

        List<Object> argList = new ArrayList<>(Arrays.asList(args));
        addParamName(sb, methodSignature.getParameterNames());
        printLog(printLog, sb, argList.toArray());

    }

    private void addParamName(StringBuilder sb, String[] parameterNames) {
        if (parameterNames == null || parameterNames.length == 0) {
            return;
        }
        for (int i = 0; i < parameterNames.length; i++) {
            sb.append(parameterNames[i]).append(":").append("{}");
            if (i < parameterNames.length - 1) {
                sb.append(",");
            }
        }
    }

    /**
     * 添加方法信息和remark
     *
     * @param sb              log context
     * @param printLog        log annotation
     * @param methodSignature method signature
     */
    private void getMethodMessage(StringBuilder sb, PrintLog printLog, MethodSignature methodSignature,String packageName) {
        sb.append("[ ").append(packageName).append(".").append(methodSignature.getMethod().getName()).append("() ]    ");
        sb.append("logHelper.traceId.[").append(getTraceId()).append("]   ");
        if (!printLog.remark().isEmpty()) {
            sb.append("remark:[").append(printLog.remark()).append("]   ");
        }
    }

    /**
     * 执行并打印返回值
     *
     * @param printLog log annotation
     * @param point    point
     */
    private Object printResultLog(String traceId ,PrintLog printLog, ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        StopWatch stopWatch = new StopWatch(methodSignature.getMethod().getName());
        stopWatch.start(methodSignature.getMethod().getName());
        Object proceed;
        try {
            proceed = point.proceed();
        } catch (Exception e) {
            onException(printLog, point, e);
            throw e;
        }
        stopWatch.stop();
        logger.info("{}", stopWatch.prettyPrint());
        if (!printLog.printResult()) {
            // 不需要打印返回值的情况
            return point.proceed();
        }
        try {
            StringBuilder sb = new StringBuilder("[logHelper.traceId:").append(traceId).append("]");

            String packageName = point.getTarget().getClass().getPackage().getName() + point.getTarget().getClass().getName();
            getMethodMessage(sb, printLog, methodSignature, packageName);

            if (proceed != null) {
                sb.append("{},");
                printLog(printLog, sb, proceed);
            } else {
                printLog(printLog, sb);
            }
        } catch (Exception e) {
            logger.debug("printResultLog exception on ", e);
        }
        return proceed;
    }

    private void onException(PrintLog printLog, ProceedingJoinPoint point, Exception e) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        OnExceptionHandler onExceptionHandler = printLog.onException().getDeclaredConstructor().newInstance();
        for (Class<?> c : printLog.exceptException()) {
            if (e.getClass().equals(c.getDeclaringClass())) {
                return;
            }
        }
        onExceptionHandler.onException(point, e, printLog.exception());
    }

    /**
     * 获取traceId
     * @return
     */
    public String getTraceId() {
        return "[logHelper.traceId:" + UUID.randomUUID().toString().replace("-", "") + "]";
    }

    /**
     * 打印日志
     *
     * @param printLog   log annotation
     * @param logContext log context
     */
    private void printLog(PrintLog printLog, StringBuilder logContext, Object... objects) {
        if (objects == null) return;
        Object[] args = new Object[objects.length];
        for (int i = 0; i < objects.length; i++) {
            try {
                args[i] = hiddenMapper.writeValueAsString(objects[i]);
            } catch (JsonProcessingException e) {
                args[i] = attemptClone(objects[i]);
            }
        }

        switch (printLog.level()) {
            case TRACE:
                logger.trace(logContext.toString(), args);
                break;
            case DEBUG:
                logger.debug(logContext.toString(), args);
                break;
            case WARN:
                logger.warn(logContext.toString(), args);
                break;
            case ERROR:
                logger.error(logContext.toString(), args);
                break;
            case INFO:
            default:
                logger.info(logContext.toString(), args);
        }
    }

    private Object attemptClone(Object object) {
        try {
            return HiddenBeanUtil.getClone(object);
        } catch (Exception e) {
            return object;
        }
    }
}