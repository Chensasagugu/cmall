package com.chen.gulimallproduct;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.gulimallproduct.entity.BrandEntity;
import com.chen.gulimallproduct.entity.CategoryEntity;
import com.chen.gulimallproduct.service.BrandService;
import com.chen.gulimallproduct.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;
    @Test
    void contextLoads() {
        List<CategoryEntity> list = categoryService.listTree();
        for (CategoryEntity entity:list)
            System.out.println(entity);
    }

    @Test
    void testStringRedisTemplate()
    {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //保存
        ops.set("hello","world_"+ UUID.randomUUID().toString());
        //查询
        String s = ops.get("hello");
        System.out.println(s);
    }

    @Test
    public void redisson()
    {
        System.out.println(redissonClient);
    }
}
