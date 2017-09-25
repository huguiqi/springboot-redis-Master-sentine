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



报的有jedis的错误，公司领导断定是因为事务的原因，认为云服务器上的redis是不支持事务的，而springboot的Cache接口是支持事务的，所以会报错，让水货副总监往去除事务的方向去解决。
足足折腾了他们两天。

[网上也有人讨论过这个问题](https://github.com/CodisLabs/codis/issues/678)

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
    public RedisTemplate<String, String> redisTemplate( ) {
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
    }```