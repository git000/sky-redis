package com.dream.skyredis.task;

import com.dream.skyredis.component.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class RedisTask {

    @Autowired
    private RedisUtil redisUtil;

    @Async
    @Scheduled(cron = "0/1 * * * * ?")
    public void methodString() {
        int i = 0;
        redisUtil.set("auth","wangxintian");
    }

//    @Async
//    @Scheduled(cron = "0/1 * * * * ?")
//    public void methodList() {
//
//    }
//
//    @Async
//    @Scheduled(cron = "0/1 * * * * ?")
//    public void methodSet() {
//
//    }
}
