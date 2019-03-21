package com.wh.mobile.service;

import com.alibaba.fastjson.JSONObject;
import com.wh.mobile.dao.RedisDao;
import com.wh.mobile.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sam on 2019/3/17.
 */
@Service
public class UserService {

    @Autowired
    private RedisDao<UserInfo> redisDao;

    @Cacheable(cacheNames = "UserInfoCache",key = "#name")
    public UserInfo getUserInfo(String name) {
        UserInfo userInfo = this.getUserInfoBy(name);
        return userInfo;
    }

    private UserInfo getUserInfoBy(String name) {
        if ("张三".equals(name)){
            return new UserInfo(name,"男","18","180",null);
        }
        return new UserInfo(name,"女","18","160",null);
    }

    public List<UserInfo> createQueue(String key) {

        for (int i = 0; i < 10; i++) {
            UserInfo userInfo = new UserInfo("小黑"+i,"男","19","18"+i,i);
            redisDao.push(key, userInfo,2l);
        }

        return redisDao.range(key,0,9,UserInfo.class);
    }

    public void consumerQueue(String test) {
        long leg = redisDao.length(test);
        for (int i = 0; i < leg; i++) {
             UserInfo userInfo = redisDao.popLast(test,UserInfo.class);
            System.out.println(userInfo.getName());
        }
    }

    @Transactional
    public void pushForTrasation(String key,boolean exceptionEnable) {
        for (int i = 0; i < 10; i++) {
            UserInfo userInfo = new UserInfo("小黑"+i,"男","19","18"+i,i);
            redisDao.push(key, userInfo,2l);
            if (exceptionEnable && i==9){
                throw new RuntimeException("插入时数据格式不正确,row_num:"+i);
            }
        }
    }
}
