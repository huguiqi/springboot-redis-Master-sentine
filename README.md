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


## jedis是什么？

Jedis 是Redis 的Java客户端程序，是对redis客户端的java封装。

jedis基本实现redis的所有功能，并且jedis在客户端实现redis数据分片功能，Redis本身是没有数据分布功能。

jedis的使用非常简单，就不介绍了。


之前公司遇到一个连接redis的一个问题，因为测试环境的redis服务器跟阿里云服务器上的redis可能部署有所不同，导致默认的springboot的redis请求报错：

```
org.springframework.data.redis.RedisConnectionFailureException: Unexpected end of stream.; nested exception is redis.clients.jedis.exceptions.JedisConnectionException: Unexpected end of stream.

	at org.springframework.data.redis.connection.jedis.JedisExceptionConverter.convert(JedisExceptionConverter.java:67)
	at org.springframework.data.redis.connection.jedis.JedisExceptionConverter.convert(JedisExceptionConverter.java:41)
	at org.springframework.data.redis.PassThroughExceptionTranslationStrategy.translate(PassThroughExceptionTranslationStrategy.java:37)
	at org.springframework.data.redis.FallbackExceptionTranslationStrategy.translate(FallbackExceptionTranslationStrategy.java:37)
	at org.springframework.data.redis.connection.jedis.JedisConnection.convertJedisAccessException(JedisConnection.java:242)
	at org.springframework.data.redis.connection.jedis.JedisConnection.exec(JedisConnection.java:796)
	at org.springframework.data.redis.connection.DefaultStringRedisConnection.exec(DefaultStringRedisConnection.java:252)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.data.redis.core.CloseSuppressingInvocationHandler.invoke(CloseSuppressingInvocationHandler.java:57)
	at com.sun.proxy.$Proxy92.exec(Unknown Source)
	at org.springframework.data.redis.cache.RedisCache$RedisCachePutCallback.doInRedis(RedisCache.java:795)
	at org.springframework.data.redis.cache.RedisCache$RedisCachePutCallback.doInRedis(RedisCache.java:767)
	at org.springframework.data.redis.cache.RedisCache$AbstractRedisCacheCallback.doInRedis(RedisCache.java:565)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:207)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:169)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:157)
	at org.springframework.data.redis.cache.RedisCache.put(RedisCache.java:226)
	at org.springframework.data.redis.cache.RedisCache.put(RedisCache.java:194)
	at org.springframework.cache.interceptor.AbstractCacheInvoker.doPut(AbstractCacheInvoker.java:85)
	at org.springframework.cache.interceptor.CacheAspectSupport$CachePutRequest.apply(CacheAspectSupport.java:784)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:417)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:327)
	at org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:61)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673)
	at com.example.demo.service.PersonDataService$$EnhancerBySpringCGLIB$$f1d4a306.getHouse(<generated>)
	at com.example.demo.RedisApplicationTests.testCacheAnnotaion(RedisApplicationTests.java:46)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:252)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:94)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:191)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)
Caused by: redis.clients.jedis.exceptions.JedisConnectionException: Unexpected end of stream.
	at redis.clients.util.RedisInputStream.ensureFill(RedisInputStream.java:199)
	at redis.clients.util.RedisInputStream.readByte(RedisInputStream.java:40)
	at redis.clients.jedis.Protocol.process(Protocol.java:151)
	at redis.clients.jedis.Protocol.read(Protocol.java:215)
	at redis.clients.jedis.Connection.readProtocolWithCheckingBroken(Connection.java:340)
	at redis.clients.jedis.Connection.getAll(Connection.java:310)
	at redis.clients.jedis.Transaction.exec(Transaction.java:44)
	at org.springframework.data.redis.connection.jedis.JedisConnection.exec(JedisConnection.java:790)
	... 52 more
    
    ```



第二天运维重启了下redis服务器，又报如下错误：

```
    org.springframework.data.redis.RedisConnectionFailureException: Unknown reply: H; nested exception is redis.clients.jedis.exceptions.JedisConnectionException: Unknown reply: H

	at org.springframework.data.redis.connection.jedis.JedisExceptionConverter.convert(JedisExceptionConverter.java:67)
	at org.springframework.data.redis.connection.jedis.JedisExceptionConverter.convert(JedisExceptionConverter.java:41)
	at org.springframework.data.redis.PassThroughExceptionTranslationStrategy.translate(PassThroughExceptionTranslationStrategy.java:37)
	at org.springframework.data.redis.FallbackExceptionTranslationStrategy.translate(FallbackExceptionTranslationStrategy.java:37)
	at org.springframework.data.redis.connection.jedis.JedisConnection.convertJedisAccessException(JedisConnection.java:242)
	at org.springframework.data.redis.connection.jedis.JedisConnection.exists(JedisConnection.java:815)
	at org.springframework.data.redis.connection.DefaultStringRedisConnection.exists(DefaultStringRedisConnection.java:264)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.data.redis.core.CloseSuppressingInvocationHandler.invoke(CloseSuppressingInvocationHandler.java:57)
	at com.sun.proxy.$Proxy92.exists(Unknown Source)
	at org.springframework.data.redis.cache.RedisCache$1.doInRedis(RedisCache.java:176)
	at org.springframework.data.redis.cache.RedisCache$1.doInRedis(RedisCache.java:172)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:207)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:169)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:157)
	at org.springframework.data.redis.cache.RedisCache.get(RedisCache.java:172)
	at org.springframework.data.redis.cache.RedisCache.get(RedisCache.java:133)
	at org.springframework.cache.interceptor.AbstractCacheInvoker.doGet(AbstractCacheInvoker.java:71)
	at org.springframework.cache.interceptor.CacheAspectSupport.findInCaches(CacheAspectSupport.java:537)
	at org.springframework.cache.interceptor.CacheAspectSupport.findCachedItem(CacheAspectSupport.java:503)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:389)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:327)
	at org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:61)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673)
	at com.example.demo.service.PersonDataService$$EnhancerBySpringCGLIB$$3ba7b95e.getHouse(<generated>)
	at com.example.demo.RedisApplicationTests.testCacheAnnotaion(RedisApplicationTests.java:46)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:252)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:94)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:191)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)
    Caused by: redis.clients.jedis.exceptions.JedisConnectionException: Unknown reply: H
	at redis.clients.jedis.Protocol.process(Protocol.java:164)
	at redis.clients.jedis.Protocol.read(Protocol.java:215)
	at redis.clients.jedis.Connection.readProtocolWithCheckingBroken(Connection.java:340)
	at redis.clients.jedis.Connection.getIntegerReply(Connection.java:265)
	at redis.clients.jedis.BinaryJedis.exists(BinaryJedis.java:279)
	at org.springframework.data.redis.connection.jedis.JedisConnection.exists(JedisConnection.java:813)
	... 52 more
	 
	 ```
**第二个错误是因为redis服务器没有启动成功，所以会报这个错误。**

报的有jedis的错误，公司领导断定是因为事务的原因，认为云服务器上的redis是不支持事务的，而springboot的Cache接口是支持事务的，所以会报错，让水货副总监往去除事务的方向去解决。
足足折腾了他们三天。

[网上也有人讨论过这个问题](https://github.com/CodisLabs/codis/issues/678)


## 解决方案

原来的config配置是：

 ```
    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        rcm.setDefaultExpiration(60);
        rcm.setUsePrefix(true);
        rcm.setCachePrefix(new DefaultRedisCachePrefix("HGQ_"));
        return rcm;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    }```



我认为这个不一定跟事务有关系，所以我就尝试去解决下，花了半天时间，刚开始将RedisCacheManager换成了GuavaCacheManager(认为它不支持事务，也是往事务的方向找问题),后来发现不对，Guava是google的内存数据库，也是就是和memenche差不多的。

最后花了一天时间，终于找到解决方案，将Jedis的客户程序去替换原来默认的connectionFactory.

代码改为：

 ```
    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        rcm.setDefaultExpiration(60);
        rcm.setUsePrefix(true);
        rcm.setCachePrefix(new DefaultRedisCachePrefix("HGQ_"));
        return rcm;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }
    
    ```
    
    
   也就是说将redisTemplate的connectionFactory换成JedisConnectionFactory就一切ok了。
   
   
   