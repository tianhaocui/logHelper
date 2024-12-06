package com.logHelper.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logHelper.annotation.Hidden;
import com.logHelper.annotation.PrintLog;
import com.logHelper.handler.HiddenFieldHandler;
import com.logHelper.util.HiddenBeanUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    private static final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new HiddenFieldHandler());
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
        //去除不需要打印的参数
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
                    try {
                        String s = mapper.writeValueAsString(argList.get(i));
                        argList.set(i, s);

                    } catch (JsonProcessingException e) {
                        argList.set(i, HiddenBeanUtil.replace(argList.get(i).toString(), annotation.dataType(), annotation.regexp()));
                    }
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
        if (!printLog.remark().isEmpty()) {
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
                Object clone = HiddenBeanUtil.getClone(proceed);
                sb.append("{},");
                printLog(printLog, sb.toString(), clone);
                logger.info("{}", stopWatch.prettyPrint());
                return proceed;
            }
            printLog(printLog, sb.toString(), null);
            logger.info("{}", stopWatch.prettyPrint());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            try {
                String s = mapper.writeValueAsString(objects[i]);
                args[i] = s;

            } catch (JsonProcessingException e) {
                args[i] = HiddenBeanUtil.getClone(objects[i]);
            }
//            args[i] = HiddenBeanUtil.getClone(objects[i]);
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

    public static void main(String[] args) {

        Integer integer = 1;
        try {
            String student = mapper.writeValueAsString(new A("123", "123"));
            System.out.println(student);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class A {
        private String email;;
        private String phone;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

}
