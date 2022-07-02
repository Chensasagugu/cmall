package com.chen.cmallproduct.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author chen
 * @date 2022.05.09 10:47
 */
@Configuration
public class MyredissonConfig {
    /**
     * 所有对Redisson的使用都是用RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        //配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");
        //创建实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
