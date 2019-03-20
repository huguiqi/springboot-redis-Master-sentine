package com.wh.mobile.controller;/**
 * Created by sam on 2019/3/17.
 */


import com.wh.mobile.model.UserInfo;
import com.wh.mobile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/")
public class TestCacheController {


    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Map> create() {
        return null;
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable("name") String name) {
        return ResponseEntity.ok(userService.getUserInfo(name));
    }

    @GetMapping("/createQueue")
    public ResponseEntity<List<UserInfo>> createQueue(@RequestParam("key") String key) {
        List<UserInfo> list =userService.createQueue(key);
        return ResponseEntity.ok(list);
    }
}


	
	
