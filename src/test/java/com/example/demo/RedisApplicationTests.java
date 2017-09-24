package com.example.demo;

import com.example.demo.bean.House;
import com.example.demo.repository.PersonDataRepository;
import com.example.demo.service.PersonDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisApplicationTests.class);

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private PersonDataService dataService;

	@Autowired
	private CountDownLatch countDownLatch;

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

	@Test
	public void testSubscribe() throws InterruptedException {


		LOGGER.info("Sending message...");

		for (int i = 0; i < 3 ; i++) {
			redisTemplate.convertAndSend("chat", "Hello from Redis!"+i);
			countDownLatch.await();
		}


		System.exit(0);
	}

}
