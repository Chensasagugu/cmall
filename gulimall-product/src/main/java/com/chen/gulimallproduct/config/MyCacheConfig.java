package com.chen.gulimallproduct.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.*;

/**
 * @author chen
 * @date 2022.05.11 11:16
 */
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class MyCacheConfig {

    /**
     * 配置文件的配置不会用上
     * @return
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        //将配置文件中的所有配置生效
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if(redisProperties.getTimeToLive()!=null)
            config = config.entryTtl(redisProperties.getTimeToLive());
        if(redisProperties.getKeyPrefix()!=null)
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        if(redisProperties.isCacheNullValues())
            config = config.disableCachingNullValues();
        if(!redisProperties.isUseKeyPrefix())
            config = config.disableKeyPrefix();
        return config;
    }
}
