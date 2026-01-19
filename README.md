# LogHelper - Spring Boot 日志增强工具

<div align="center">

[![Maven Central](https://img.shields.io/maven-central/v/io.github.tianhaocui/loghelper-spring-boot-start.svg)](https://search.maven.org/artifact/io.github.tianhaocui/loghelper-spring-boot-start)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8%2B%20%7C%2017%2B-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.x%20%7C%203.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

一个功能强大的 Spring Boot AOP 日志增强工具，提供方法日志打印、字段脱敏、异常告警、性能监控等企业级功能。

[快速开始](#快速开始) • [功能特性](#功能特性) • [配置说明](#配置说明) • [使用示例](#使用示例) • [常见问题](#常见问题)

</div>

---

## ✨ 功能特性

### 🎯 核心功能

- **📝 @PrintLog** - 自动打印方法参数、返回值、执行时间
- **🔐 @Hidden** - 敏感字段自动脱敏（手机号、身份证、密码等）
- **🚨 异常告警** - 支持微信、钉钉、飞书、邮件多渠道告警
- **📊 @Monitor** - 性能监控与调用链追踪
- **🌐 @PrintCurl** - 自动生成 HTTP 请求的 curl 命令
- **🔗 TraceId** - 全链路请求追踪
- **⚡ 零侵入** - 基于 AOP，无需修改业务代码

### 🎁 额外特性

- ✅ 支持 Spring Boot 2.x 和 3.x
- ✅ 支持 Java 8 和 Java 17+
- ✅ 异常排除机制（unException）
- ✅ 自定义异常处理器
- ✅ 异步告警发送
- ✅ 线程池配置
- ✅ 堆栈信息过滤

---

## 🚀 快速开始

### 1. 添加依赖

#### Maven

**Spring Boot 3.x (Java 17+)：**
```xml
<dependency>
    <groupId>io.github.tianhaocui</groupId>
    <artifactId>loghelper-spring-boot-start</artifactId>
    <version>2.1.1-RELEASE</version>
</dependency>
```

**Spring Boot 2.x (Java 8)：**
```xml
<dependency>
    <groupId>io.github.tianhaocui</groupId>
    <artifactId>loghelper-spring-boot-start</artifactId>
    <version>2.0.6-java8</version>
</dependency>
```

#### Gradle

```groovy
// Spring Boot 3.x
implementation 'io.github.tianhaocui:loghelper-spring-boot-start:2.1.1-RELEASE'

// Spring Boot 2.x
implementation 'io.github.tianhaocui:loghelper-spring-boot-start:2.0.6-java8'
```

### 2. 启用 LogHelper

在 Spring Boot 启动类上添加 `@EnableLogHelper` 注解：

```java
@SpringBootApplication
@EnableLogHelper  // 👈 添加这个注解
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. 基础使用

```java
@RestController
@RequestMapping("/api")
public class UserController {

    // 自动打印参数和返回值
    @PrintLog
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    // 异常时自动发送微信告警
    @PrintLog(
        remark = "用户创建接口",
        onException = WechatAlertHandler.class
    )
    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }
}
```

**日志输出示例：**
```
[loghelper.traceId:abc123def456] [UserController.getUser]   remark:[]id:{}
1001
[loghelper.traceId:abc123def456] [UserController.getUser]  result:{}
{"id":1001,"name":"张三","phone":"138****5678"}
```

---

## 📖 详细功能说明

### 1️⃣ @PrintLog - 方法日志打印

自动打印方法的参数、返回值、执行时间，支持异常告警。

#### 基础用法

```java
@PrintLog
public User getUser(Long id) {
    return userService.getById(id);
}
```

#### 高级配置

```java
@PrintLog(
    // 日志级别：TRACE, DEBUG, INFO, WARN, ERROR
    level = PrintLog.Level.INFO,

    // 备注信息
    remark = "用户查询接口",

    // 是否打印参数（默认 true）
    printParameter = true,

    // 是否打印返回值（默认 true）
    printResult = true,

    // 异常处理器（支持微信、钉钉、飞书、邮件）
    onException = WechatAlertHandler.class,

    // 排除的异常类型（这些异常不会触发告警）
    unException = {IllegalArgumentException.class, NullPointerException.class},

    // 不触发 onException 的异常类型
    exceptException = {NothingException.class},

    // 异常时的额外参数
    exceptionParam = {"userId", "orderId"},

    // 使用 SpEL 表达式提取异常值
    exceptionValue = "#user.id"
)
public User createUser(User user) {
    return userService.create(user);
}
```

#### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `level` | Level | INFO | 日志级别 |
| `remark` | String | "" | 备注信息，会显示在日志中 |
| `printParameter` | boolean | true | 是否打印方法参数 |
| `printResult` | boolean | true | 是否打印返回值 |
| `onException` | Class | DefaultOnExceptionHandler | 异常处理器 |
| `unException` | Class[] | {} | 排除的异常类型 |
| `exceptException` | Class[] | {NothingException} | 不触发 onException 的异常 |
| `exceptionParam` | String[] | {} | 异常时的参数 |
| `exceptionValue` | String | "" | SpEL 表达式，提取异常值 |

---

### 2️⃣ @Hidden - 字段脱敏

自动对敏感字段进行脱敏处理，支持多种脱敏策略。

#### 使用方式

```java
public class User {
    private Long id;
    private String name;

    // 手机号脱敏：138****5678
    @Hidden(type = HiddenType.MOBILE)
    private String phone;

    // 身份证脱敏：110***********1234
    @Hidden(type = HiddenType.ID_CARD)
    private String idCard;

    // 邮箱脱敏：abc***@example.com
    @Hidden(type = HiddenType.EMAIL)
    private String email;

    // 密码完全隐藏：******
    @Hidden(type = HiddenType.PASSWORD)
    private String password;

    // 自定义脱敏：保留前3后4位
    @Hidden(type = HiddenType.CUSTOM, prefixLength = 3, suffixLength = 4)
    private String bankCard;
}
```

#### 支持的脱敏类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `MOBILE` | 手机号：保留前3后4位 | 138****5678 |
| `ID_CARD` | 身份证：保留前3后4位 | 110***********1234 |
| `EMAIL` | 邮箱：保留前3位和域名 | abc***@example.com |
| `PASSWORD` | 密码：完全隐藏 | ****** |
| `NAME` | 姓名：保留姓氏 | 张** |
| `ADDRESS` | 地址：保留前6位 | 北京市朝阳区****** |
| `BANK_CARD` | 银行卡：保留前4后4位 | 6222****1234 |
| `CUSTOM` | 自定义：指定保留位数 | 可配置 |

---

### 3️⃣ 异常告警

支持多种告警渠道：微信、钉钉、飞书、邮件。

#### 3.1 微信告警

**配置：**
```yaml
spring:
  loghelper:
    alert:
      package-prefix: com.example,com.myapp
      wechat:
        webhook: https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=YOUR_KEY
```

**使用：**
```java
@PrintLog(onException = WechatAlertHandler.class)
public void processOrder(Order order) {
    // 业务逻辑
}
```

**告警消息示例：**
```markdown
### processOrder exception alert

> {"orderId": "12345", "amount": 99.99}

**exception type：** java.lang.RuntimeException

**exception traceId：** abc123def456

**exception message：** 库存不足

**stack trace：**
 - com.example.service.OrderService.processOrder(OrderService.java:42)
 - com.example.controller.OrderController.createOrder(OrderController.java:28)
```

#### 3.2 钉钉告警

**配置：**
```yaml
spring:
  loghelper:
    alert:
      package-prefix: com.example
      dingtalk:
        webhook: https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN
        secret: YOUR_SECRET  # 可选，加签密钥
```

**使用：**
```java
@PrintLog(onException = DingtalkAlertHandler.class)
public void processPayment(Payment payment) {
    // 业务逻辑
}
```

#### 3.3 飞书告警

**配置：**
```yaml
spring:
  loghelper:
    alert:
      package-prefix: com.example
      feishu:
        webhook: https://open.feishu.cn/open-apis/bot/v2/hook/YOUR_TOKEN
        secret: YOUR_SECRET  # 可选，加签密钥
```

**使用：**
```java
@PrintLog(onException = FeishuAlertHandler.class)
public void processRefund(Refund refund) {
    // 业务逻辑
}
```

#### 3.4 邮件告警

**配置：**
```yaml
spring:
  loghelper:
    alert:
      package-prefix: com.example
      email:
        host: smtp.example.com
        port: 465
        ssl: true
        username: alert@example.com
        password: your-password
        from-name: System Alert
        to: admin1@example.com,admin2@example.com
        cc: manager@example.com
```

**使用：**
```java
@PrintLog(onException = EmailAlertHandler.class)
public void batchProcess() {
    // 批处理逻辑
}
```

#### 3.5 自定义告警处理器

```java
public class CustomAlertHandler extends OnExceptionHandler {

    @Override
    public void onException(ProceedingJoinPoint point,
                           Exception e,
                           Class<? extends Exception>[] exception,
                           String[] exceptionParam,
                           Object o) {
        // 自定义告警逻辑
        String methodName = point.getSignature().getName();
        String errorMessage = e.getMessage();

        // 发送到自定义渠道
        sendToCustomChannel(methodName, errorMessage, e);
    }

    private void sendToCustomChannel(String method, String message, Exception e) {
        // 实现你的告警逻辑
    }
}
```

**使用：**
```java
@PrintLog(onException = CustomAlertHandler.class)
public void yourMethod() {
    // 业务逻辑
}
```

---

### 4️⃣ @Monitor - 性能监控

监控方法执行时间，记录调用链，自动识别慢方法。

#### 基础用法

```java
@Monitor
public List<Order> queryOrders(String userId) {
    return orderDao.findByUserId(userId);
}
```

#### 高级配置

```java
@Monitor(
    // 监控点名称
    name = "订单查询",

    // 慢方法阈值（毫秒），超过此值会告警
    threshold = 500,

    // 是否记录参数
    recordParams = true,

    // 是否记录返回值
    recordResult = false
)
public List<Order> queryOrders(String userId) {
    return orderDao.findByUserId(userId);
}
```

**性能监控配置：**
```yaml
spring:
  loghelper:
    performance:
      enabled: true        # 是否启用性能监控
      threshold: 1000      # 全局慢方法阈值（毫秒）
```

**日志输出示例：**
```
[PERFORMANCE] OrderService.queryOrders executed in 1234ms (threshold: 500ms)
[CALL_CHAIN]
└─ OrderService.queryOrders [1234ms]
   ├─ OrderDao.findByUserId [890ms]
   └─ CacheService.get [344ms]
```

---

### 5️⃣ @PrintCurl - 打印 curl 命令

自动生成 HTTP 请求的 curl 命令，便于调试和问题重现。

#### 使用方式

```java
@RestController
public class ApiController {

    @PrintCurl
    @PostMapping("/api/external")
    public String callExternalApi(@RequestBody Request request) {
        // 调用外部 API
        return restTemplate.postForObject(url, request, String.class);
    }
}
```

**日志输出：**
```bash
curl -X POST 'https://api.example.com/v1/orders' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer xxx' \
  -d '{"orderId":"12345","amount":99.99}'
```

---

### 6️⃣ TraceId - 全链路追踪

自动为每个请求生成唯一的 TraceId，便于日志追踪和问题排查。

#### 配置

```yaml
spring:
  loghelper:
    enable-trace: true  # 启用 TraceId（默认 true）
```

#### 日志格式

```
[loghelper.traceId:abc123def456] [UserController.getUser] 用户查询
[loghelper.traceId:abc123def456] [UserService.getById] 从数据库查询用户
[loghelper.traceId:abc123def456] [UserDao.selectById] SQL: SELECT * FROM user WHERE id = ?
```

#### 在代码中获取 TraceId

```java
String traceId = LogHelperTraceHandler.getTraceLog();
```

---

## ⚙️ 完整配置说明

### application.yml 完整配置

```yaml
spring:
  loghelper:
    # 是否启用 TraceId（默认 true）
    enable-trace: true

    # 是否启用参数打印（默认 true）
    enable-parameter-print: true

    # 告警配置
    alert:
      # 包名前缀，用于过滤堆栈信息（多个用逗号分隔）
      package-prefix: com.example,com.myapp

      # 企业微信告警
      wechat:
        webhook: https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=YOUR_KEY

      # 钉钉告警
      dingtalk:
        webhook: https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN
        secret: YOUR_SECRET  # 可选，加签密钥

      # 飞书告警
      feishu:
        webhook: https://open.feishu.cn/open-apis/bot/v2/hook/YOUR_TOKEN
        secret: YOUR_SECRET  # 可选，加签密钥

      # 邮件告警
      email:
        host: smtp.example.com
        port: 465
        ssl: true
        username: alert@example.com
        password: your-password
        from-name: System Alert
        to: admin1@example.com,admin2@example.com
        cc: manager@example.com

    # 性能监控配置
    performance:
      enabled: true        # 是否启用性能监控（默认 true）
      threshold: 1000      # 慢方法阈值（毫秒，默认 1000）

    # 异步告警线程池配置（一般无需修改）
    thread-pool:
      core-pool-size: 1
      max-pool-size: 1
      queue-capacity: 100
      thread-name-prefix: LogHelper-Async-
```

---

## 📚 使用示例

### 示例 1：用户服务

```java
@Service
public class UserService {

    /**
     * 用户注册 - 打印参数、返回值，异常时发送微信告警
     */
    @PrintLog(
        remark = "用户注册",
        onException = WechatAlertHandler.class,
        unException = {IllegalArgumentException.class}  // 参数校验异常不告警
    )
    public User register(UserRegisterDTO dto) {
        // 参数校验
        if (dto.getPhone() == null) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        // 业务逻辑
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());

        return userDao.insert(user);
    }

    /**
     * 用户查询 - 性能监控
     */
    @Monitor(threshold = 500)
    public User getById(Long id) {
        return userDao.selectById(id);
    }
}
```

### 示例 2：订单服务

```java
@Service
public class OrderService {

    /**
     * 订单创建 - 多渠道告警
     */
    @PrintLog(
        remark = "订单创建 - 核心业务",
        level = PrintLog.Level.ERROR,
        onException = DingtalkAlertHandler.class
    )
    public Order createOrder(OrderCreateDTO dto) {
        // 检查库存
        if (!checkStock(dto.getProductId(), dto.getQuantity())) {
            throw new BusinessException("库存不足");
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setProductId(dto.getProductId());
        order.setQuantity(dto.getQuantity());

        return orderDao.insert(order);
    }

    /**
     * 支付处理 - 使用 SpEL 提取异常值
     */
    @PrintLog(
        remark = "支付处理",
        onException = FeishuAlertHandler.class,
        exceptionValue = "#order.orderId",
        exceptionParam = {"orderId", "amount"}
    )
    public void processPayment(Order order, BigDecimal amount) {
        // 支付逻辑
        paymentService.pay(order.getOrderId(), amount);
    }
}
```

### 示例 3：数据模型脱敏

```java
@Data
public class User {
    private Long id;
    private String username;

    @Hidden(type = HiddenType.MOBILE)
    private String phone;

    @Hidden(type = HiddenType.EMAIL)
    private String email;

    @Hidden(type = HiddenType.ID_CARD)
    private String idCard;

    @Hidden(type = HiddenType.PASSWORD)
    private String password;

    @Hidden(type = HiddenType.CUSTOM, prefixLength = 4, suffixLength = 4)
    private String bankCard;
}
```

**打印效果：**
```json
{
  "id": 1001,
  "username": "zhangsan",
  "phone": "138****5678",
  "email": "zha***@example.com",
  "idCard": "110***********1234",
  "password": "******",
  "bankCard": "6222****1234"
}
```

---

## ❓ 常见问题

### Q1: 告警没有发送，如何排查？

**A:** 按以下步骤排查：

1. **开启 DEBUG 日志**：
```yaml
logging:
  level:
    com.loghelper: DEBUG
```

2. **检查配置是否生效**：
```java
@Autowired
private LogHelperProperties properties;

@GetMapping("/test-config")
public String testConfig() {
    return properties.getAlert().getWechat().getWebhook();
}
```

3. **检查异常是否被全局异常处理器拦截**：
   - 如果有 `@RestControllerAdvice`，异常可能在到达方法前就被拦截
   - 确保异常在方法内部抛出

4. **手动测试告警功能**：
```java
@GetMapping("/test-alert")
public String testAlert() {
    RuntimeException e = new RuntimeException("测试告警");
    WeChatAlertUtil.sendAlert("测试标题", "测试消息", e);
    return "已发送";
}
```

### Q2: 如何获取企业微信机器人 Webhook？

**A:**
1. 打开企业微信，进入需要接收告警的群聊
2. 点击右上角 `···` → 群机器人 → 添加机器人
3. 创建机器人，复制 Webhook 地址
4. 将地址配置到 `spring.loghelper.alert.wechat.webhook`

### Q3: 支持同时配置多个告警渠道吗？

**A:** 可以在不同的方法上使用不同的告警渠道：

```java
// 订单服务用微信告警
@PrintLog(onException = WechatAlertHandler.class)
public void createOrder() { }

// 支付服务用钉钉告警
@PrintLog(onException = DingtalkAlertHandler.class)
public void processPayment() { }

// 批处理任务用邮件告警
@PrintLog(onException = EmailAlertHandler.class)
public void batchJob() { }
```

### Q4: 如何排除某些异常不告警？

**A:** 使用 `unException` 参数：

```java
@PrintLog(
    onException = WechatAlertHandler.class,
    unException = {
        IllegalArgumentException.class,    // 参数校验异常
        ValidationException.class,         // 数据验证异常
        NotFoundException.class            // 资源未找到异常
    }
)
public User createUser(User user) {
    // 业务逻辑
}
```

### Q5: 日志打印影响性能吗？

**A:**
- 日志打印采用异步方式，对性能影响很小
- JSON 序列化使用 Jackson，性能优异
- 可以通过配置关闭参数打印：
```java
@PrintLog(printParameter = false, printResult = false)
```

### Q6: 脱敏后的数据会影响数据库存储吗？

**A:** 不会。`@Hidden` 只在日志打印时生效，不影响实际的数据处理和存储。

### Q7: TraceId 如何在微服务间传递？

**A:** 需要配合 Spring Cloud Sleuth 或手动在 HTTP 请求头中传递：

```java
@Bean
public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(Collections.singletonList(
        (request, body, execution) -> {
            String traceId = LogHelperTraceHandler.getTraceLog();
            request.getHeaders().add("X-Trace-Id", traceId);
            return execution.execute(request, body);
        }
    ));
    return restTemplate;
}
```

### Q8: 如何在非 Spring 管理的类中使用？

**A:** 使用工具类直接调用：

```java
// 发送告警
WeChatAlertUtil.sendAlert("标题", "消息", exception);

// 获取配置
LogHelperProperties properties = LogHelperPropertiesUtil.getProperties();

// 获取 TraceId
String traceId = LogHelperTraceHandler.getTraceLog();
```

---

## 🔄 版本兼容性

| LogHelper 版本 | Spring Boot 版本 | Java 版本 | 说明 |
|---------------|-----------------|-----------|------|
| 2.1.x | 3.x | 17+ | 最新版本，推荐使用 |
| 2.0.x-java8 | 2.x | 8+ | Java 8 兼容版本 |

### 升级指南

从 2.0.x 升级到 2.1.x：
1. 升级 Spring Boot 到 3.x
2. 升级 Java 到 17+
3. 修改依赖版本为 2.1.1-RELEASE
4. 将 `javax.*` 包名改为 `jakarta.*`

---

## 📝 更新日志

### v2.1.1 (2024-11-29)
- 🐛 修复 WeChatAlertUtil 配置获取问题
- ✨ 优化异常告警性能
- 📖 完善文档和使用示例

### v2.1.0 (2024-02-11)
- ✨ 支持 Spring Boot 3.x
- ✨ 支持 Java 17+
- ✨ 新增性能监控功能 @Monitor
- ✨ 新增调用链追踪
- 🐛 修复若干已知问题

### v2.0.6-java8 (2023-11-29)
- ✨ 新增飞书告警支持
- ✨ 优化脱敏逻辑
- 📖 完善文档

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

---

## 📄 License

本项目基于 [MIT License](LICENSE) 开源。

---

## 👥 联系方式

- 作者：cuitianhao
- 邮箱：hoopoo@163.com
- GitHub：https://github.com/tianhaocui/logHelper

---

## ⭐ Star History

如果这个项目对你有帮助，请给它一个 Star ⭐️

[![Star History Chart](https://api.star-history.com/svg?repos=tianhaocui/logHelper&type=Date)](https://star-history.com/#tianhaocui/logHelper&Date)

---

<div align="center">

**[⬆ 回到顶部](#loghelper---spring-boot-日志增强工具)**

Made with ❤️ by [cuitianhao](https://github.com/tianhaocui)

</div>
