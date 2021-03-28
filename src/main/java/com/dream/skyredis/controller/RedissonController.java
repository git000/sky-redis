package com.dream.skyredis.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/redisson")
@RestController
public class RedissonController {

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping("/testRedisson")
    public String testRedisson(){

        //获取分布式锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("lock");

        //加锁（阻塞等待），默认过期时间是30秒
        lock.lock();
        try{
            //如果业务执行过长，Redisson会自动给锁续期
            Thread.sleep(5000);
            System.out.println("加锁成功，执行业务逻辑");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            lock.unlock();
        }

        return "Hello Redisson!";
    }
}
