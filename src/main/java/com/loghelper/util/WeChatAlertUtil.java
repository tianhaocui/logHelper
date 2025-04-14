package com.loghelper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghelper.configuration.LogHelperProperties;
import com.loghelper.handler.LogHelperTraceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信告警工具类
 */
@Slf4j
@RequiredArgsConstructor
public class WeChatAlertUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final LogHelperProperties properties = SpringContextUtil.getBean(LogHelperProperties.class);

    /**
     * 发送告警消息
     *
     * @param title 标题
     * @param message 消息内容
     * @param e 异常
     */
    public static void sendAlert(String title, String message, Exception e) {
        LogHelperProperties.Alert alert = properties.getAlert();
        LogHelperProperties.Alert.WechatAlert wechat = alert.getWechat();
        if (StringUtils.isEmpty(wechat.getWebhook())) {
            log.debug("WeChat alert is disabled or webhook is not configured");
            return;
        }

        try {
            Map<String, Object> body = new HashMap<>(2);
            body.put("msgtype", "markdown");

            // 构建告警内容
            StringBuilder content = new StringBuilder();
            content.append("### ").append(title).append(" exception alert\n\n");
            content.append("> ").append(message).append("\n\n");
            content.append("**exception type：**").append(e.getClass().getName()).append("\n\n");
            content.append("**exception traceId：**").append(LogHelperTraceHandler.getTraceLog()).append("\n\n");
            content.append("**exception message：**").append(e.getMessage()).append("\n\n");
            content.append("**stack trace：**\n");

            // 获取配置的包名前缀
            String[] packages = alert.getPackagePrefix().split(",");

            // 过滤堆栈信息
            for (StackTraceElement element : e.getStackTrace()) {
                if (Arrays.stream(packages)
                        .anyMatch(pkg -> element.getClassName().startsWith(pkg.trim()))) {
                    content.append(" - ").append(element).append("\n");
                }
            }

            Map<String, String> markdown = new HashMap<>(1);
            markdown.put("content", content.toString());
            body.put("markdown", markdown);

            String jsonBody = objectMapper.writeValueAsString(body);
            String result = HttpUtil.post(wechat.getWebhook(), jsonBody);
            log.debug("Send WeChat alert result: {}", result);
        } catch (Exception ex) {
            log.error("Failed to send WeChat alert", ex);
        }
    }
}
