package com.chen.cmallproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1.整合SpringCache简化开发
 *      1）引入依赖
 *          spring-boot-starter-cache,spring-boot-starter-data-redis
 *      2）写配置
 *          （1）自动配置了哪些
 *              CacheAutoConfiguration会导入RedisCacheConfiguration;
 *              自动配好了缓存管理器RedisCacheManager
 *          （2）配置使用redis作为缓存
 *      3）测试使用缓存
 *          @Cacheable 触发将数据保存到缓存操作
 *          @CacheEvict 触发将数据从缓存删除的操作
 *          @CachePut 不影响方法执行更新缓存
 *          @Caching 组合以上多个操作
 *          @CacheConfig 在类级别共享的相同配置
 *          （1）开启缓存功能 @EnableCaching
 *           (2)只要使用注解就能完成缓存操作
 *      4）不足
 *          1.缓存穿透：查询一个null数据。解决：缓存空数据：cache-null-values=true
 *          2.缓存击穿：大量并发进来同时查询一个正好过期的数据。解决：加锁。SpringCache默认是无加锁的；@Cacheable(sync=true)可以加锁
 *          3.缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间：spring.cache.redis.time--to-alive=xx
 *      5）总结
 *          常规数据（读多写少，即时性，一致性要求不高的数据），可以用Spring-Cache；写模式，只要设置了过期时间就足够了
 *          特殊数据：特殊设计
 */
@EnableCaching
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chen.cmallproduct.feign")
@MapperScan("com.chen.cmallproduct.dao")
public class CmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallProductApplication.class, args);
    }

}
