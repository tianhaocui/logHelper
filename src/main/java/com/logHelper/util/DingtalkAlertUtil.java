package com.loghelper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghelper.configuration.LogHelperProperties;
import com.loghelper.handler.LogHelperTraceHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉告警工具类
 */
@Slf4j
public class DingtalkAlertUtil {
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
        LogHelperProperties.Alert.DingtalkAlert dingtalk = alert.getDingtalk();
        if (StringUtils.isEmpty(dingtalk.getWebhook())) {
            log.debug("Dingtalk alert is disabled or webhook is not configured");
            return;
        }

        try {
            Map<String, Object> body = new HashMap<>(2);
            body.put("msgtype", "markdown");

            // 构建告警内容
            StringBuilder content = new StringBuilder();
            content.append("### ").append(title).append(" 异常告警\n\n");
            content.append("> ").append(message).append("\n\n");
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
                    content.append(" - ").append(element).append("\n");
                }
            }

            Map<String, String> markdown = new HashMap<>(2);
            markdown.put("title", title);
            markdown.put("text", content.toString());
            body.put("markdown", markdown);

            String jsonBody = objectMapper.writeValueAsString(body);
            String webhook = dingtalk.getWebhook();

            // 如果配置了加签密钥，添加签名
            if (StringUtils.isNotEmpty(dingtalk.getSecret())) {
                webhook = addSignature(webhook, dingtalk.getSecret());
            }

            String result = HttpUtil.post(webhook, jsonBody);
            log.debug("Send Dingtalk alert result: {}", result);
        } catch (Exception ex) {
            log.error("Failed to send Dingtalk alert", ex);
        }
    }

    /**
     * 添加签名
     *
     * @param webhook webhook地址
     * @param secret  密钥
     * @return 带签名的webhook地址
     */
    private static String addSignature(String webhook, String secret) {
        try {
            long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = Base64.getEncoder().encodeToString(signData);
            return webhook + "&timestamp=" + timestamp + "&sign=" + sign;
        } catch (Exception e) {
            log.error("Failed to add signature", e);
            return webhook;
        }
    }
} 