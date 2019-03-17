package com.wh.mobile.controller;/**
 * Created by sam on 2019/3/17.
 */


import com.wh.mobile.model.UserInfo;
import com.wh.mobile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(value = "/")
public class TestCacheController {


    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Map> create() {
        return null;
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable("name") String name) {
        return ResponseEntity.ok(userService.getUserInfo(name));
    }
}


	
	
