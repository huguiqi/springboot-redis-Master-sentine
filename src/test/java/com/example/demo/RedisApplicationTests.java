package com.example.demo;

import com.example.demo.bean.House;
import com.example.demo.service.PersonDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTests {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private PersonDataService dataService;

	@Test
	public void testAdd() {
		redisTemplate.opsForValue().set("keyId","123456");
	}

	@Test
	public void testQuery(){

		System.out.printf("redis get:"+redisTemplate.opsForValue().get("keyId"));
	}

	@Test
	public void testCacheAnnotaion(){

		House house = dataService.getHouse("长沙");
		System.out.println("house is:"+house.getName());
		Assert.notNull(house,"不应该有空值");
	}

}
