package com.loghelper.model;

import lombok.Data;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法调用节点
 */
@Data
public class MethodCallNode {
    /**
     * 方法签名
     */
    private String signature;

    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 执行时间（毫秒）
     */
    private long executionTime;

    /**
     * 调用深度
     */
    private int depth;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 子节点
     */
    private List<MethodCallNode> children = new ArrayList<>();

    /**
     * 方法参数
     */
    private String parameters;

    /**
     * 返回值
     */
    private String result;

    private StopWatch stopwatch;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
} 