package com.chen.cart.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author chen
 * @date 2022.06.20 13:04
 */
@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setHashKeySerializer(new GenericFastJsonRedisSerializer());
        stringRedisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        //stringRedisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        return stringRedisTemplate;
    }
}
