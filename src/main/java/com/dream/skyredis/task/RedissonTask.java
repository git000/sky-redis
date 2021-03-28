package com.dream.skyredis.task;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class RedissonTask {

    @Autowired
    private RedissonClient redissonClient;

    @Async
    @Scheduled(cron = "0/1 * * * * ?")
    public void methodString() {
        int i = 0;
        RLock lock = redissonClient.getLock("stringLock");
        lock.lock();
        try{
            RBucket<String> token = redissonClient.getBucket("token");
            token.set("张三"+i);
            System.out.println("当前线程 = "+ Thread.currentThread().getName()  + " string 的大小：" + token.get());

            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Async
    @Scheduled(cron = "0/1 * * * * ?")
    public void methodList() {
        long i = 0;
        RLock lock = redissonClient.getLock("listLock");
        lock.lock();
        try{
            RList<String> userList = redissonClient.getList("userList");
            if(userList.isExists()){
                userList.expire(0, TimeUnit.SECONDS);
            }
            System.out.println("当前线程 = "+ Thread.currentThread().getName()  + " list 的大小：" + userList.size());
            userList.add("001");
            userList.add("002");
            userList.add("003");
            userList.add("004");

            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    @Async
    @Scheduled(cron = "0/1 * * * * ?")
    public void methodSet() {
        long i = 0;
        RLock lock = redissonClient.getLock("setLock");
        lock.lock();
        try{
            RSet<Long> userSet = redissonClient.getSet("userSet");
            System.out.println( "当前线程 = "+ Thread.currentThread().getName()  + " set 的大小："+userSet.size());
            userSet.add(i++);
            userSet.add(i++);
            userSet.add(i++);
            userSet.add(i++);
            userSet.add(i++);
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
