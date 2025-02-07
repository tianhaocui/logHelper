package com.loghelper.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.loghelper.model.MethodCallNode;
import com.loghelper.util.LogHelperPropertiesUtil;
import com.loghelper.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

/**
 * 调用链上下文
 */
@Slf4j
public class CallChainContext {
    private static final TransmittableThreadLocal<Deque<MethodCallNode>> CALL_CHAIN =
        new TransmittableThreadLocal<Deque<MethodCallNode>>() {
            @Override
            protected Deque<MethodCallNode> initialValue() {
                return new LinkedList<>();
            }
        };

    private static final TransmittableThreadLocal<Long> METHOD_THRESHOLD =
        new TransmittableThreadLocal<Long>() {
            @Override
            protected Long initialValue() {
                return -1L;
            }
        };

    private static final TransmittableThreadLocal<Integer> DEPTH =
        new TransmittableThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };

    /**
     * 开始方法调用
     */
    public static MethodCallNode begin(String signature, String params) {
        Deque<MethodCallNode> chain = CALL_CHAIN.get();
        MethodCallNode node = new MethodCallNode();
        node.setSignature(signature);
        node.setParameters(params);
        node.setStartTime(System.nanoTime());
        
        if (!chain.isEmpty()) {
            chain.peek().getChildren().add(node);
        }
        chain.push(node);
        return node;
    }

    /**
     * 结束方法调用
     */
    public static void end(String result, Throwable error) {
        Deque<MethodCallNode> chain = CALL_CHAIN.get();
        if (chain.isEmpty()) return;

        MethodCallNode node = chain.pop();
        long duration = System.nanoTime() - node.getStartTime();
        node.setExecutionTime(TimeUnit.NANOSECONDS.toMillis(duration));
        node.setSuccess(error == null);
        
        if (chain.isEmpty()) {
            logCallChain(node);
        }
    }

    private static void logPerformance(StopWatch sw) {
        LogUtil.info("Performance Breakdown:\n{}", sw.prettyPrint());

        long threshold = LogHelperPropertiesUtil.getProperties()
                        .getPerformance().getThreshold();
        if (sw.getTotalTimeMillis() > threshold) {
            LogUtil.warn("Slow call chain detected! Total time: {}ms",
                       sw.getTotalTimeMillis());
        }
    }

    /**
     * 记录调用链日志
     */
    private static void logCallChain(MethodCallNode root) {
        StringBuilder sb = new StringBuilder("\nMonitor Call Chain:\n");
        buildCallChainLog(root, sb, "");
        LogUtil.info(sb.toString());

        // 获取方法级别的阈值，使用安全获取方式
        long threshold = Optional.ofNullable(METHOD_THRESHOLD.get()).orElse(-1L);

        if (threshold == -1) {
            threshold = LogHelperPropertiesUtil.getProperties().getPerformance().getThreshold();
        }

        // 如果执行时间超过阈值，记录警告日志
        if (root.getExecutionTime() > threshold) {
            LogUtil.warn("Slow method call detected: {} took {}ms (threshold: {}ms)",
                root.getSignature(), root.getExecutionTime(), threshold);
        }
    }

    /**
     * 构建调用链日志内容
     */
    private static void buildCallChainLog(MethodCallNode node, StringBuilder sb, String indent) {
        sb.append(indent)
          .append("├─ ")
          .append(node.getSignature())
          .append(" [")
          .append(node.getExecutionTime())
          .append("ms]");

        if (!node.isSuccess()) {
            sb.append(" (ERROR: ").append(node.getErrorMessage()).append(")");
        }
        sb.append("\n");

        String childIndent = indent + "│  ";
        for (MethodCallNode child : node.getChildren()) {
            buildCallChainLog(child, sb, childIndent);
        }
    }

    public static void recordDuration(long duration) {
    }

    // 新增根上下文初始化方法
    public static void initRootContext() {
        CALL_CHAIN.get().clear();
        METHOD_THRESHOLD.set(-1L); // 明确设置默认值
    }

    private static String formatParams(String params) {
        return params != null ?
            String.format("%s", truncate(params, 100)) :
            "";
    }

    private static String buildTaskName(String signature, String params) {
        return params != null ?
            String.format("%s(%s)", signature, truncate(params, 100)) :
            signature;
    }

    private static String truncate(String str, int maxLength) {
        return str.length() > maxLength ?
            str.substring(0, maxLength) + "..." :
            str;
    }

    private static String generateDepthPrefix(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("-> ");
        }
        return sb.toString();
    }
} 