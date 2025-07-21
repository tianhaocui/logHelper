## aop打印参数和返回值日志、脱敏、告警




# logHelper

### 强烈建议使用2.1.0之后的版本

### 启动类添加@EnableLogHelper

#### @PrintCurl   
 打印curl

 
 #### @PrintLog    
 参数打印
  * exception  数组,不打印的参数名

* level      日志级别

* remark     备注

* printParameter 是否打印参数

* printResult    是否打印返回值
#### @Hidden
字段注解 脱敏打印


#### jdk8及以下使用-java8后缀的版本
```xml
         <dependency>
            <groupId>io.github.tianhaocui</groupId>
            <artifactId>loghelper-spring-boot-start</artifactId>
            <version>2.0.6-java8</version>
        </dependency>
```
#### 无-java8版本默认为高版本
```xml
         <dependency>
            <groupId>io.github.tianhaocui</groupId>
            <artifactId>loghelper-spring-boot-start</artifactId>
            <version>2.1.0-pre</version>
        </dependency>
```
```yaml
spring:
  loghelper:
    enable-trace: true                      #是否开启loghelper的traceId
    enable-parameter-print: true            #是否开启参数打印
    alert:
      package-prefix: com.example,com.myapp   #异常处理时发送通知里堆栈信息的包前缀(不在内的会被过滤,不在通知内容里)
      wechat:
        webhook: https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx #企业微信机器人的webhook
      email:
        host: smtp.example.com
        port: 465
        ssl: true
        username: alert@example.com
        password: your-password
        from-name: System Alert
        to: admin1@example.com,admin2@example.com
        cc: manager@example.com
    
    thread-pool:                   # 错误提醒的线程池配置,一般无需配置
      core-pool-size: 1
      max-pool-size: 1
      queue-capacity: 100
      thread-name-prefix: LogHelper-Async-
```
