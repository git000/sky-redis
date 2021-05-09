package com.dream.skyredis.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description redis工具类
 * @Author wxt
 * @Date 2019/11/28
 **/
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 保存
     *
     * @param key
     * @param value
     * @param time     时间
     * @param timeUnit 时间单元
     * @return
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    public boolean setIfAbsent(String key, Object value,long time,TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value,time,timeUnit);
    }
    public boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }


    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取key的过期时间，返回单位毫秒
     *
     * @param key
     * @return
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    public void hashMapPut(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);

    }


    public void hashMapPutAll(String key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public boolean hashMapHasKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public Map<Object, Object> hashMapEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Long hashMapDelete(String key, Object hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }


}
