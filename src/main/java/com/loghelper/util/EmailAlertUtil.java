package com.loghelper.util;

import com.loghelper.configuration.LogHelperProperties;
import com.loghelper.handler.LogHelperTraceHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

/**
 * 邮件告警工具类
 */
@Slf4j
public class EmailAlertUtil {
    private static final LogHelperProperties properties = LogHelperPropertiesUtil.getProperties();

    /**
     * 发送告警邮件
     *
     * @param title   标题
     * @param message 消息内容
     * @param e       异常
     */
    public static void sendAlert(String title, String message, Exception e) {
        LogHelperProperties.Alert alert = properties.getAlert();
        LogHelperProperties.Alert.EmailAlert emailAlert = alert.getEmail();


        if (
            StringUtils.isEmpty(emailAlert.getHost()) ||
            StringUtils.isEmpty(emailAlert.getUsername()) ||
            StringUtils.isEmpty(emailAlert.getTo())) {
            log.debug("Email emailAlert is disabled or not properly configured");
            return;
        }

        try {
            JavaMailSenderImpl mailSender = createMailSender();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 设置发件人
            helper.setFrom(emailAlert.getUsername(), emailAlert.getFromName());
            
            // 设置收件人
            helper.setTo(emailAlert.getTo().split(","));
            
            // 设置抄送
            if (StringUtils.isNotEmpty(emailAlert.getCc())) {
                helper.setCc(emailAlert.getCc().split(","));
            }

            // 设置主题
            helper.setSubject(title + " - Exception Alert");

            // 构建邮件内容
            StringBuilder content = new StringBuilder();
            content.append("<h3>").append(title).append(" Exception Alert</h3>");
            content.append("<p><strong>Message:</strong> ").append(message).append("</p>");
            content.append("<p><strong>TraceId:</strong> ").append(LogHelperTraceHandler.getTraceId()).append("</p>");
            content.append("<p><strong>Exception Type:</strong> ").append(e.getClass().getName()).append("</p>");
            content.append("<p><strong>Exception Message:</strong> ").append(e.getMessage()).append("</p>");
            content.append("<h4>Stack Trace:</h4>");
            content.append("<pre>");

            // 获取配置的包名前缀
            String[] packages = alert.getPackagePrefix().split(",");

            // 过滤堆栈信息
            for (StackTraceElement element : e.getStackTrace()) {
                if (Arrays.stream(packages)
                        .anyMatch(pkg -> element.getClassName().startsWith(pkg.trim()))) {
                    content.append(element).append("\n");
                }
            }
            content.append("</pre>");

            // 设置内容，使用HTML格式
            helper.setText(content.toString(), true);

            // 发送邮件
            mailSender.send(mimeMessage);
            log.debug("Email emailAlert sent successfully");
        } catch (Exception ex) {
            log.error("Failed to send email emailAlert", ex);
        }
    }

    /**
     * 创建邮件发送器
     */
    private static JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        LogHelperProperties.Alert alert = properties.getAlert();
        LogHelperProperties.Alert.EmailAlert emailAlert = alert.getEmail();

        mailSender.setHost(emailAlert.getHost());
        mailSender.setPort(emailAlert.getPort());
        mailSender.setUsername(emailAlert.getUsername());
        mailSender.setPassword(emailAlert.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        if (emailAlert.isSsl()) {
            props.put("mail.smtp.ssl.enable", "true");
        }
        props.put("mail.debug", "false");

        return mailSender;
    }
} 