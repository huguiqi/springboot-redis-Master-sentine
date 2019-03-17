package com.wh.mobile.configuration.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wh.mobile.configuration.RiosJedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;

import static java.util.Collections.singletonMap;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

/**
 * Created by guiqi on 2017/8/3.
 */

@Configuration
@EnableCaching
public class RedisConfig {

    @Autowired
    private RiosJedisConfiguration riosJedisConfiguration;


    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }


    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // static key prefix
        RedisCacheConfiguration.defaultCacheConfig().prefixKeysWith("RIOS_");

        // computed key prefix
        RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheName -> "RIOS_HOTEL_" + cacheName);

        RedisCacheManager cm = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig())
                .withInitialCacheConfigurations(singletonMap("predefined", defaultCacheConfig().disableCachingNullValues()))
                .transactionAware()
                .build();
        return cm;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(jedisConnectionFactory);
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
    JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel(riosJedisConfiguration.getSentinel().getHost(),riosJedisConfiguration.getSentinel().getPort());
        sentinelConfig.setDatabase(riosJedisConfiguration.getMaster().getDatabase());
//
        sentinelConfig.setPassword(RedisPassword.of(riosJedisConfiguration.getSentinel().getPassword()));
//        sentinelConfig.setPassword(RedisPassword.none());


        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig,jedisPoolConfig);
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);

//        jedisConnectionFactory.getStandaloneConfiguration().setHostName(riosJedisConfiguration.getMaster().getHost());
//        jedisConnectionFactory.getStandaloneConfiguration().setPort(riosJedisConfiguration.getMaster().getPort());
//        jedisConnectionFactory.getStandaloneConfiguration().setDatabase(riosJedisConfiguration.getMaster().getDatabase());
//        jedisConnectionFactory.getStandaloneConfiguration().setPassword(RedisPassword.of(riosJedisConfiguration.getMaster().getPassword()));
//        jedisConnectionFactory.getStandaloneConfiguration().setPassword(RedisPassword.none());
//        JedisPool pool = new JedisPool(jedisPoolConfig, riosJedisConfiguration.getMaster().getHost(), riosJedisConfiguration.getMaster().getPort(), 10000, riosJedisConfiguration.getMaster().getPassword(), 0);
        System.out.println("clientName:"+jedisConnectionFactory.getClientName());
        return jedisConnectionFactory;
    }


    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(riosJedisConfiguration.getMaster().getPool().getMaxIdle());
        jedisPoolConfig.setMaxTotal(riosJedisConfiguration.getMaster().getPool().getMaxTotal());
        jedisPoolConfig.setMinIdle(riosJedisConfiguration.getMaster().getPool().getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(riosJedisConfiguration.getMaster().getTimeout());
        jedisPoolConfig.setTestOnBorrow(true);
        return jedisPoolConfig;
    }




    //todo 后续要配置jedis客户端读写分离

}
