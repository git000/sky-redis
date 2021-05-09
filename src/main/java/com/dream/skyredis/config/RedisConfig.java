package com.dream.skyredis.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 1.整理spring集成redis配置
 * 2.整理spring集成redis缓存配置
 * <p>
 * spring-redis中使用了RedisTemplate来进行redis的操作，通过泛型的K和V设置键值对的对象类型。这里使用了string作为key的对象类型，值为Object。
 * 对于Object，spring-redis默认使用了jdk自带的序列化，不推荐使用默认了。所以使用了json的序列化方式
 * 对spring-redis对redis的五种数据类型也有支持
 * HashOperations：对hash类型的数据操作
 * ValueOperations：对redis字符串类型数据操作
 * ListOperations：对链表类型的数据操作
 * SetOperations：对无序集合类型的数据操作
 * ZSetOperations：对有序集合类型的数据操作
 *

 * @Cacheable 触发缓存入口, 一般用于查询操作，根据key查询缓存.该注解是可以将缓存分类，它是类级别的注解方式
 * @CacheEvict 触发移除缓存, 根据key删除缓存中的数据。allEntries=true表示删除缓存中的所有数据。
 * @CachePut 更新缓存, 一般用于更新和插入操作，每次都会请求db(1. 如果key存在，更新内容  2. 如果key不存在，插入内容。)
 * @Caching 将多种缓存操作分组, 通过注解的属性值可以看出来，这个注解将其他注解方式融合在一起了，我们可以根据需求来自定义注解，并将前面三个注解应用在一起
 * @CacheConfig 类级别的缓存注解，允许共享缓存名称
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    private static final String CACHE_PRIFIX = "AUTH:";//缓存总前缀

    /*
     *定义缓存数据 key 生成策略的bean
     */
    @Bean
    public KeyGenerator myKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(":");
                sb.append(method.getName());
                sb.append(":");
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };

    }

//    /**
//     * 默认jdk序列化缓存,缓存的对象需要序列化
//     * @author jzc 2019年7月16日
//     * @param connectionFactory
//     * @return
//     */
//    @Bean
//    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        //初始化一个RedisCacheWriter
//        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
//        //设置默认超过期时间是30秒
//        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
//        //初始化RedisCacheManager
//        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
//        return cacheManager;
//    }

//    /**
//     * 自定义cacheManager缓存管理器
//       * 非定制化,所有缓存统一走该配置和失效时间
//     * @author jzc 2019年7月16日
//     * @param factory
//     * @return
//     */
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory factory)
//    {
//    	//序列化定义,默认使用JdkSerializationRedisSerializer
//        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
//        //解决查询缓存转换异常的问题
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        //设置默认超过期时间是30秒
//        Duration ofSeconds = Duration.ofSeconds(30);//Duration.ZERO永久缓存
//        //配置序列化(解决二进制乱码的问题)
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(ofSeconds)
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
//                .disableCachingNullValues()//空值不缓存
//                .computePrefixWith(cacheName -> CACHE_PRIFIX.concat(cacheName).concat(":"));
//        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
//                .cacheDefaults(config)
//                .build();
//        return cacheManager;
//    }

    /**
     * 自定义cacheManager缓存管理器
     * 定制化缓存管理器
     * 1.被定制的缓存空间,使用自己的配置和过期时间
     * 2.未被定制的缓存空间,则使用默认的配置和过期时间
     *
     * @param factory
     * @return
     * @author jzc 2019年7月16日
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        //序列化定义,默认使用JdkSerializationRedisSerializer
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //设置默认缓存时间是30s
        Duration ofSeconds = Duration.ofSeconds(30);//Duration.ZERO永久缓存
        //默认你缓存配置(序列化,过期时间,缓存头)
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ofSeconds)//默认缓存30s
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
            .disableCachingNullValues()//空值不缓存,如果方法返回null,会报错;所以方法上添加非空缓存条件@Cacheable(unless="#result == null")
            .computePrefixWith(cacheName -> CACHE_PRIFIX.concat(cacheName).concat(":"));

        // 设置一个初始化的定制缓存空间set集合
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("objectCache");
        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("objectCache", config.entryTtl(Duration.ofSeconds(60)));//缓存60s

        // 使用自定义的缓存配置初始化一个cacheManager
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
            .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
            .withInitialCacheConfigurations(configMap)//初始定制化缓存空间配置
            .cacheDefaults(config)//非定制,默认配置
            .build();
        return cacheManager;
    }


    /**
     * redis的操作模板类定义序列化
     *
     * @param factory
     * @return
     * @author jzc 2019年7月16日
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //配置连接工厂
        template.setConnectionFactory(factory);
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        //指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        //值采用json序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 对hash类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}