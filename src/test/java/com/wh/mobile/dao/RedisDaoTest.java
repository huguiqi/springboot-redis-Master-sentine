package com.wh.mobile.dao;

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
public class RedisDaoTest {

    @Autowired
    private RedisDao<UserInfo> redisDao;

    @Test
    public void push() throws Exception {
        redisDao.push("test", new UserInfo("小王八","女","30","180",90),1l);
        Assert.assertNotNull(redisDao.range("test",0,1, UserInfo.class));
    }

    @Test
    public void popLast() throws Exception {
    }

}