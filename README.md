# spring-boot-muiltSource

springboot + web + mybatis + h2 双数据源配置，支持分布式事务处理


## redis

Mac 下安装：

 1. 官网http://redis.io/ 下载最新的稳定版本,这里是3.2.0
 2. sudu mv 到 /usr/local/
 3. sudo tar -zxf redis-3.2.0.tar 解压文件
 4. 进入解压后的目录 cd redis-3.2.0
 5. sudo make test 测试编译
 6. sudo make install 
 

启动：
mac:
    
    redis-server
     
windows:
      
      ./redis-server.exe redis.windows.conf
      
客户端连接：
    
    redis-cli -h 127.0.0.1 -p 6379
    
进入连接：

设置和获取cache:

    set myKey abc
    get myKey


## redis 发布订阅



[link](http://www.jianshu.com/p/a2ab17707eff)


### annotation-based caching


[spring关于annotation cache的注解介绍](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html)



[annotation Cache注解中SpEL语法介绍](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html)



[我的springboot集成项目地址](https://github.com/huguiqi/springboot-redis.git)