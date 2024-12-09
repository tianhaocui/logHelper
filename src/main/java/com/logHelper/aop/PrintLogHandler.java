package com.logHelper.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logHelper.annotation.Hidden;
import com.logHelper.annotation.PrintLog;
import com.logHelper.handler.HiddenFieldHandler;
import com.logHelper.util.HiddenBeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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
    private final ObjectMapper mapper = new ObjectMapper();

    {
        //todo 支持自定义的序列化方式和自定义模块装载
        mapper.registerModule(new HiddenFieldHandler());
        mapper.registerModule(new JavaTimeModule());
    }


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
            logger.debug("printLog exception on ", e);
        }
        return result;
    }

    /**
     * 打印参数
     *
     * @param printLog 需要打印的log
     * @param point 切点
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

        List<Object> argList = new ArrayList<>(Arrays.asList(args));

        printLog(printLog, sb.toString(), argList.toArray());

    }


    /**
     * 添加方法信息和remark
     *
     * @param sb    log context
     * @param printLog log annotation
     * @param methodSignature method signature
     */
    private void getMethodMessage(StringBuilder sb, PrintLog printLog, MethodSignature methodSignature, String packageName) {
        sb.append("[ ").append(packageName).append(".").append(methodSignature.getMethod().getName()).append("() ]    ");
        if (!printLog.remark().isEmpty()) {
            sb.append("remark.[").append(printLog.remark()).append("]   ");
        }
    }

    /**
     * 执行并打印返回值
     *
     * @param printLog log annotation
     * @param point    point
     */
    private Object printResultLog(PrintLog printLog, ProceedingJoinPoint point) throws Throwable {
        if (!printLog.printResult()) {
            return point.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        StopWatch stopWatch = new StopWatch(methodSignature.getMethod().getName());
        stopWatch.start(methodSignature.getMethod().getName());
        Object proceed = point.proceed();
        stopWatch.stop();
        try {
            StringBuilder sb = new StringBuilder();
            String packageName = point.getTarget().getClass().getPackage().getName();
            getMethodMessage(sb, printLog, methodSignature, packageName);

            if (proceed != null) {
                String s = mapper.writeValueAsString(proceed);

                sb.append("{},");
                printLog(printLog, sb.toString(), s);
                logger.info("{}", stopWatch.prettyPrint());
            } else {
                printLog(printLog, sb.toString());
                logger.info("{}", stopWatch.prettyPrint());
            }
        } catch (Exception e) {
            logger.debug("printResultLog exception on ", e);
        }
        return proceed;
    }

    /**
     * 打印日志
     *
     * @param printLog      log annotation
     * @param logContext    log context
     */
    private void printLog(PrintLog printLog, String logContext, Object... objects) {
        if (objects == null) return;
        Object[] args = new Object[objects.length];
        for (int i = 0; i < objects.length; i++) {
            try {
                args[i] = mapper.writeValueAsString(objects[i]);
            } catch (JsonProcessingException e) {
                args[i] = attemptClone(objects[i]);
            }
        }

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

    private Object attemptClone(Object object) {
        try {
            return HiddenBeanUtil.getClone(object);
        } catch (Exception e) {
            return object;
        }
    }
}