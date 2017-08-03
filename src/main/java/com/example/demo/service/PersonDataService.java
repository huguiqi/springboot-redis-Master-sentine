package com.example.demo.service;

import com.example.demo.bean.House;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by guiqi on 2017/8/3.
 */
@Service
public class PersonDataService {

    @Cacheable(value = "houseCache", key = "#city.toString()")
    public House getHouse(String city) {
        System.out.println("无缓存的时候调用这里---数据库查询");
        return new House("绿城","100","长沙");
    }


}
