## aop实现的使用注解打印参数和返回值日志，添加脱敏




# logHelper

<img width="1351" alt="image" src="https://user-images.githubusercontent.com/54015884/185789440-54b554de-bca0-424e-9c34-ae23115b7073.png">

启动类需要添加@EnableLogHelper

 @PrintCurl   打印curl
 * exception  数组,不打印的参数名

* level      日志级别

* remark     备注

* printParameter 是否打印参数

* printResult    是否打印返回值
 
 @PrintLog    参数打印
 
 @Hidden      脱敏打印
 


