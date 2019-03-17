package com.wh.mobile.service;

import com.wh.mobile.model.UserInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by sam on 2019/3/17.
 */
@Service
public class UserService {

    @Cacheable(cacheNames = "UserInfoCache",key = "#name")
    public UserInfo getUserInfo(String name) {
        UserInfo userInfo = this.getUserInfoBy(name);
        return userInfo;
    }

    private UserInfo getUserInfoBy(String name) {
        if ("张三".equals(name)){
            return new UserInfo(name,"男","18","180");
        }
        return new UserInfo(name,"女","18","160");
    }
}
