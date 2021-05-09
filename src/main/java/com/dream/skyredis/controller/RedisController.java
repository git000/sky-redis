package com.dream.skyredis.controller;

import com.dream.skyredis.component.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/redis")
@RestController
public class RedisController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/testRedis")
    public String testRedis(){

        Object auth = redisUtil.get("auth");


        return "Hello Redis!";
    }
}