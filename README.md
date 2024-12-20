## aop实现的使用注解打印参数和返回值日志，添加脱敏




# logHelper


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
            <version>2.0.6-pre</version>
        </dependency>
```
