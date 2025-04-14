package com.loghelper.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LogHelper 配置属性
 */
@Data
@ConfigurationProperties(prefix = "spring.loghelper")
public class LogHelperProperties {

    /**
     * 是否启用日志追踪
     */
    private boolean enableTrace = true;

    /**
     * 是否启用参数打印
     */
    private boolean enableParameterPrint = true;

    private Alert alert = new Alert();

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    /**
     * 性能监控配置
     */
    private Performance performance = new Performance();

    @Data
    public static class Alert {

        /**
         * 邮件告警配置
         */
        private EmailAlert email = new EmailAlert();

        /**
         * 企业微信告警配置
         */
        private WechatAlert wechat = new WechatAlert();

        /**
         * 钉钉告警配置
         */
        private DingtalkAlert dingtalk = new DingtalkAlert();

        /**
         * 飞书告警配置
         */
        private FeishuAlert feishu = new FeishuAlert();

        /**
         * 包名前缀，多个用逗号分隔 用于过滤日志、堆栈信息等
         */
        private String packagePrefix = "";

        @Data
        public static class WechatAlert {

            /**
             * 企业微信机器人 webhook 地址
             */
            private String webhook;
        }

        @Data
        public static class EmailAlert {

            /**
             * SMTP 服务器地址
             */
            private String host;

            /**
             * SMTP 端口
             */
            private int port = 25;

            /**
             * 是否启用 SSL
             */
            private boolean ssl = false;

            /**
             * 发件人邮箱
             */
            private String username;

            /**
             * 发件人密码或授权码
             */
            private String password;

            /**
             * 发件人显示名称
             */
            private String fromName = "Exception Alert";

            /**
             * 收件人列表，多个用逗号分隔
             */
            private String to;

            /**
             * 抄送列表，多个用逗号分隔
             */
            private String cc;
        }

        @Data
        public static class DingtalkAlert {

            /**
             * 钉钉机器人 webhook 地址
             */
            private String webhook;

            /**
             * 加签密钥
             */
            private String secret;
        }

        @Data
        public static class FeishuAlert {

            /**
             * 飞书机器人 webhook 地址
             */
            private String webhook;

            /**
             * 加签密钥
             */
            private String secret;
        }
    }

    @Data
    public static class ThreadPool {

        /**
         * 核心线程数
         */
        private int corePoolSize = 1;

        /**
         * 最大线程数
         */
        private int maxPoolSize = 1;

        /**
         * 队列容量
         */
        private int queueCapacity = 100;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix = "LogHelper-Async-";
    }

    @Data
    public static class Performance {

        /**
         * 是否启用性能监控
         */
        private boolean enabled = true;

        /**
         * 慢方法阈值（毫秒）
         */
        private long threshold = 1000;
    }
}
