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


## redis的持久化方式

Redis有两种持久化的方式：快照（RDB文件）和追加式文件（AOF文件）：

* RDB持久化方式会在一个特定的间隔保存那个时间点的一个数据快照。
* AOF持久化方式则会记录每一个服务器收到的写操作。在服务启动时，这些记录的操作会逐条执行从而重建出原来的数据。写操作命令记录的格式跟Redis协议一致，以追加的方式进行保存。
* Redis的持久化是可以禁用的，就是说你可以让数据的生命周期只存在于服务器的运行时间里。
两种方式的持久化是可以同时存在的，但是当Redis重启时，AOF文件会被优先用于重建数据。

## RDB

工作原理

* Redis调用fork()，产生一个子进程。
* 子进程把数据写到一个临时的RDB文件。
* 当子进程写完新的RDB文件后，把旧的RDB文件替换掉。

**优点**

* RDB文件是一个很简洁的单文件，它保存了某个时间点的Redis数据，很适合用于做备份。你可以设定一个时间点对RDB文件进行归档，这样就能在需要的时候很轻易的把数据恢复到不同的版本。
* 基于上面所描述的特性，RDB很适合用于灾备。单文件很方便就能传输到远程的服务器上。
* RDB的性能很好，需要进行持久化时，主进程会fork一个子进程出来，然后把持久化的工作交给子进程，自己不会有相关的I/O操作。
* 比起AOF，在数据量比较大的情况下，RDB的启动速度更快。

**缺点**

* RDB容易造成数据的丢失。假设每5分钟保存一次快照，如果Redis因为某些原因不能正常工作，那么从上次产生快照到Redis出现问题这段时间的数据就会丢失了。
* RDB使用fork()产生子进程进行数据的持久化，如果数据比较大的话可能就会花费点时间，造成Redis停止服务几毫秒。如果数据量很大且CPU性能不是很好的时候，停止服务的时间甚至会到1秒。

### 文件路径和名称

默认Redis会把快照文件存储为当前目录下一个名为dump.rdb的文件。要修改文件的存储路径和名称，可以通过修改配置文件redis.conf实现：

### RDB文件名，默认为dump.rdb。
    
    dbfilename dump.rdb

### 文件存放的目录，AOF文件同样存放在此目录下。默认为当前工作目录。
    dir ./
    
保存点（RDB的启用和禁用）
你可以配置保存点，使Redis如果在每N秒后数据发生了M次改变就保存快照文件。例如下面这个保存点配置表示每60秒，如果数据发生了1000次以上的变动，Redis就会自动保存快照文件：

save 60 1000
保存点可以设置多个，Redis的配置文件就默认设置了3个保存点：

格式为：save <seconds> <changes> 可以设置多个。

``` 
    save 900 1 #900秒后至少1个key有变动
    save 300 10 #300秒后至少10个key有变动
    save 60 10000 #60秒后至少10000个key有变动
```

如果想禁用快照保存的功能，可以通过注释掉所有"save"配置达到，或者在最后一条"save"配置后添加如下的配置：

    save ""
    
### 错误处理
默认情况下，如果Redis在后台生成快照的时候失败，那么就会停止接收数据，目的是让用户能知道数据没有持久化成功。但是如果你有其他的方式可以监控到Redis及其持久化的状态，那么可以把这个功能禁止掉。

    stop-writes-on-bgsave-error yes
    
### 数据压缩
   
   默认Redis会采用LZF对数据进行压缩。如果你想节省点CPU的性能，你可以把压缩功能禁用掉，但是数据集就会比没压缩的时候要打。

        rdbcompression yes
        
### 数据校验

从版本5的RDB的开始，一个CRC64的校验码会放在文件的末尾。这样更能保证文件的完整性，但是在保存或者加载文件时会损失一定的性能（大概10%）。如果想追求更高的性能，可以把它禁用掉，这样文件在写入校验码时会用0替代，加载的时候看到0就会直接跳过校验。

        rdbchecksum yes
    
### 手动生成快照
Redis提供了两个命令用于手动生成快照。

### SAVE

SAVE命令会使用同步的方式生成RDB快照文件，这意味着在这个过程中会阻塞所有其他客户端的请求。因此不建议在生产环境使用这个命令，除非因为某种原因需要去阻止Redis使用子进程进行后台生成快照（例如调用fork(2)出错）。

### BGSAVE

BGSAVE命令使用后台的方式保存RDB文件，调用此命令后，会立刻返回OK返回码。Redis会产生一个子进程进行处理并立刻恢复对客户端的服务。在客户端我们可以使用LASTSAVE命令查看操作是否成功。

    127.0.0.1:6379> BGSAVE
    Background saving started
    127.0.0.1:6379> LASTSAVE
    (integer) 1433936394

配置文件里禁用了快照生成功能不影响SAVE和BGSAVE命令的效果。

## AOF

快照并不是很可靠。如果你的电脑突然宕机了，或者电源断了，又或者不小心杀掉了进程，那么最新的数据就会丢失。而AOF文件则提供了一种更为可靠的持久化方式。每当Redis接受到会修改数据集的命令时，就会把命令追加到AOF文件里，当你重启Redis时，AOF里的命令会被重新执行一次，重建数据。

**优点**

* 比RDB可靠。你可以制定不同的fsync策略：不进行fsync、每秒fsync一次和每次查询进行fsync。默认是每秒fsync一次。这意味着你最多丢失一秒钟的数据。
* AOF日志文件是一个纯追加的文件。
就算是遇到突然停电的情况，也不会出现日志的定位或者损坏问题。甚至如果因为某些原因（例如磁盘满了）命令只写了一半到日志文件里，我们也可以用redis-check-aof这个工具很简单的进行修复。
* 当AOF文件太大时，Redis会自动在后台进行重写。重写很安全，因为重写是在一个新的文件上进行，同时Redis会继续往旧的文件追加数据。新文件上会写入能重建当前数据集的最小操作命令的集合。当新文件重写完，Redis会把新旧文件进行切换，然后开始把数据写到新文件上。
* AOF把操作命令以简单易懂的格式一条接一条的保存在文件里，很容易导出来用于恢复数据。例如我们不小心用FLUSHALL命令把所有数据刷掉了，只要文件没有被重写，我们可以把服务停掉，把最后那条命令删掉，然后重启服务，这样就能把被刷掉的数据恢复回来。

**缺点**

* 在相同的数据集下，AOF文件的大小一般会比RDB文件大。
* 在某些fsync策略下，AOF的速度会比RDB慢。通常fsync设置为每秒一次就能获得比较高的性能，而在禁止fsync的情况下速度可以达到RDB的水平。
* 在过去曾经发现一些很罕见的BUG导致使用AOF重建的数据跟原数据不一致的问题。

### 启用AOF

把配置项appendonly设为yes：

        appendonly yes
        
### 文件路径和名称

文件存放目录，与RDB共用。默认为当前工作目录。

    dir ./

默认文件名为appendonly.aof
        
        appendfilename "appendonly.aof"

### 可靠性

你可以配置Redis调用fsync的频率，有三个选项：

* 每当有新命令追加到AOF的时候调用fsync。速度最慢，但是最安全。
* 每秒fsync一次。速度快（2.4版本跟快照方式速度差不多），安全性不错（最多丢失1秒的数据）。
* 从不fsync，交由系统去处理。这个方式速度最快，但是安全性一般。

推荐使用每秒fsync一次的方式（默认的方式），因为它速度快，安全性也不错。相关配置如下：

    # appendfsync always
    appendfsync everysec
    # appendfsync no
    
### 日志重写

随着写操作的不断增加，AOF文件会越来越大。例如你递增一个计数器100次，那么最终结果就是数据集里的计数器的值为最终的递增结果，但是AOF文件里却会把这100次操作完整的记录下来。而事实上要恢复这个记录，只需要1个命令就行了，也就是说AOF文件里那100条命令其实可以精简为1条。所以Redis支持这样一个功能：在不中断服务的情况下在后台重建AOF文件。

工作原理如下：

Redis调用fork()，产生一个子进程。
子进程把新的AOF写到一个临时文件里。
主进程持续把新的变动写到内存里的buffer，同时也会把这些新的变动写到旧的AOF里，这样即使重写失败也能保证数据的安全。
当子进程完成文件的重写后，主进程会获得一个信号，然后把内存里的buffer追加到子进程生成的那个新AOF里。
Redis
我们可以通过配置设置日志重写的条件：

* Redis会记住自从上一次重写后AOF文件的大小（如果自Redis启动后还没重写过，则记住启动时使用的AOF文件的大小）。
* 如果当前的文件大小比起记住的那个大小超过指定的百分比，则会触发重写。
* 同时需要设置一个文件大小最小值，只有大于这个值文件才会重写，以防文件很小，但是已经达到百分比的情况。

        
        auto-aof-rewrite-percentage 100
        auto-aof-rewrite-min-size 64mb
        
要禁用自动的日志重写功能，我们可以把百分比设置为0：

    auto-aof-rewrite-percentage 0
    
Redis 2.4以上才可以自动进行日志重写，之前的版本需要手动运行BGREWRITEAOF这个命令。

## 数据损坏修复

如果因为某些原因（例如服务器崩溃）AOF文件损坏了，导致Redis加载不了，可以通过以下方式进行修复：

1. 备份AOF文件。

2. 使用redis-check-aof命令修复原始的AOF文件：
    
      
      $ redis-check-aof --fix
    
可以使用diff -u命令看下两个文件的差异。

3. 使用修复过的文件重启Redis服务。

## 从RDB切换到AOF
这里只说Redis >= 2.2版本的方式：

备份一个最新的dump.rdb的文件，并把备份文件放在一个安全的地方。
运行以下两条命令：
    
    $ redis-cli config set appendonly yes
    $ redis-cli config set save ""

确保数据跟切换前一致。
确保数据正确的写到AOF文件里。
第二条命令是用来禁用RDB的持久化方式，但是这不是必须的，因为你可以同时启用两种持久化方式。
记得对配置文件redis.conf进行编辑启用AOF，因为命令行方式修改配置在重启Redis后就会失效。

## 备份
建议的备份方法：
* 创建一个定时任务，每小时和每天创建一个快照，保存在不同的文件夹里。
* 定时任务运行时，把太旧的文件进行删除。例如只保留48小时的按小时创建的快照和一到两个月的按天创建的快照。
* 每天确保一次把快照文件传输到数据中心外的地方进行保存，至少不能保存在Redis服务所在的服务器。

[参考文章](https://segmentfault.com/a/1190000002906345)



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
** 第二个错误是因为redis服务器没有启动成功，所以会报这个错误。**

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
   
   
   ## RedisTemplate和StringRedisTemplate的区别
   
   