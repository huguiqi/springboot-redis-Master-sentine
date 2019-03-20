package com.wh.mobile.service;

import com.alibaba.fastjson.JSONObject;
import com.wh.mobile.dao.RedisDao;
import com.wh.mobile.model.UserInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by sam on 2019/3/20.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private RedisDao<UserInfo> redisDao;

    @Autowired
    private UserService userService;

    @Test
    public void createQueue() throws Exception {
        System.out.println(userService.createQueue("test"));
        Assert.assertNotNull(redisDao.range("test",0,9, UserInfo.class));
//        userService.consumerQueue("test");
    }

    @Test
    public void testQueuePopLast(){
        redisDao.push("test", new UserInfo("小王八","女","30","180",90),1l);
        Assert.assertEquals("小王八",redisDao.popHead("test",UserInfo.class).getName());

    }

    @Test
    public void testConsumeQueue(){
        userService.consumerQueue("test");
    }

}