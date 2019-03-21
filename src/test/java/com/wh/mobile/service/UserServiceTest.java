package com.wh.mobile.service;

import com.wh.mobile.dao.RedisDao;
import com.wh.mobile.model.UserInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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
        System.out.println(userService.createQueue("test1"));
        Assert.assertNotNull(redisDao.range("test1",0,9, UserInfo.class));
//        userService.consumerQueue("test");
    }

    @Test
    public void testQueuePopLast(){
        redisDao.push("test", new UserInfo("小王八","女","30","180",90),3l);
        Assert.assertEquals("小王八",redisDao.popHead("test",UserInfo.class).getName());

    }

    @Test
    @Transactional
    public void testQueuePushForTrasation(){
        //由于redis将一个redisTempelate的实例请求当作一个事务，所以在本方法结束以后才会提交事务，(其实是还没执行,push后也就没有返回值)所以在刚插入一条进去以后，立马查询是查询不到的，必须要等事务提交后，再次查询才有
        Long rows = redisDao.push("test3", new UserInfo("小王八2","女","30","180",90),3l);
        System.out.println("effect rows :"+ rows);
        Assert.assertNull(rows);
        //因在是在test方法上加的trasactional注解，就代表test方法执行完后会对redis进行回滚
    }

    @Test(expected = RuntimeException.class)
    public void testUserServicePushForTrasation(){
        userService.pushForTrasation("trasationTest",false);
        Assert.assertNotNull(redisDao.popLast("trasationTest",UserInfo.class));

        userService.pushForTrasation("trasationTest2",true);
        Assert.assertNull(redisDao.popHead("trasationTest2",UserInfo.class));
    }

    @Test
    public void testConsumeQueue(){
        userService.consumerQueue("test");
    }

}