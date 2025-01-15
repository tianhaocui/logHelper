package com.loghelper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghelper.configuration.LogHelperProperties;
import com.loghelper.handler.LogHelperTraceHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 飞书告警工具类
 */
@Slf4j
public class FeishuAlertUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final LogHelperProperties properties = LogHelperPropertiesUtil.getProperties();

    /**
     * 发送告警消息
     *
     * @param title   标题
     * @param message 消息内容
     * @param e       异常
     */
    public static void sendAlert(String title, String message, Exception e) {
        LogHelperProperties.Alert alert = properties.getAlert();
        LogHelperProperties.Alert.FeishuAlert feishu = alert.getFeishu();
        if (StringUtils.isEmpty(feishu.getWebhook())) {
            log.debug("Feishu alert is disabled or webhook is not configured");
            return;
        }

        try {
            Map<String, Object> body = new HashMap<>(3);
            body.put("msg_type", "interactive");

            // 构建告警内容
            Map<String, Object> card = new HashMap<>(2);
            Map<String, Object> header = new HashMap<>(2);
            Map<String, Object> headerTitle = new HashMap<>(2);
            headerTitle.put("tag", "plain_text");
            headerTitle.put("content", title + " 异常告警");
            header.put("title", headerTitle);
            card.put("header", header);

            // 构建内容元素
            Map<String, Object> elements = new HashMap<>(1);
            elements.put("tag", "markdown");
            StringBuilder content = new StringBuilder();
            content.append(message).append("\n\n");
            content.append("**异常类型：**").append(e.getClass().getName()).append("\n\n");
            content.append("**追踪ID：**").append(LogHelperTraceHandler.getTraceLog()).append("\n\n");
            content.append("**异常信息：**").append(e.getMessage()).append("\n\n");
            content.append("**堆栈信息：**\n");

            // 获取配置的包名前缀
            String[] packages = alert.getPackagePrefix().split(",");

            // 过滤堆栈信息
            for (StackTraceElement element : e.getStackTrace()) {
                if (Arrays.stream(packages)
                        .anyMatch(pkg -> element.getClassName().startsWith(pkg.trim()))) {
                    content.append("- ").append(element).append("\n");
                }
            }
            elements.put("content", content.toString());
            card.put("elements", Collections.singletonList(elements));

            body.put("card", card);

            // 如果配置了加签密钥，添加签名
            if (StringUtils.isNotEmpty(feishu.getSecret())) {
                long timestamp = System.currentTimeMillis() / 1000;
                String sign = generateSign(timestamp, feishu.getSecret());
                body.put("timestamp", timestamp);
                body.put("sign", sign);
            }

            String jsonBody = objectMapper.writeValueAsString(body);
            String result = HttpUtil.post(feishu.getWebhook(), jsonBody);
            log.debug("Send Feishu alert result: {}", result);
        } catch (Exception ex) {
            log.error("Failed to send Feishu alert", ex);
        }
    }

    /**
     * 生成签名
     *
     * @param timestamp 时间戳（秒）
     * @param secret    密钥
     * @return 签名
     */
    private static String generateSign(long timestamp, String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (Exception e) {
            log.error("Failed to generate sign", e);
            return "";
        }
    }
} 