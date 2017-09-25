package com.example.demo.service;

import com.example.demo.bean.House;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by guiqi on 2017/8/3.
 */
@Service
public class PersonDataService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Cacheable(value = "houseCache", key = "#city.toString()")
    public House getHouse(String city) {
        System.out.println("无缓存的时候调用这里---数据库查询");
        return new House("绿城","100","长沙");
    }

    public static void main(String[] args) {
        GenericObjectPoolConfig config = new JedisPoolConfig();
        JedisPool pool = new JedisPool(config, "10.7.10.221");
        try (Jedis jedis = pool.getResource()) {
            jedis.set("test", "aaa");
            System.out.println(jedis.get("test"));
        }
        pool.close();
    }
}
