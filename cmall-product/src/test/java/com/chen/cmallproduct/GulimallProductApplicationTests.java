package com.chen.cmallproduct;


import com.chen.cmallproduct.entity.CategoryEntity;
import com.chen.cmallproduct.service.*;
import com.chen.cmallproduct.vo.web.SkuItemVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

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

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SkuInfoService skuInfoService;
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

    //测试得到所有基本属性值
    @Test
    public void getAllBaseAttrValue()
    {
        List<SkuItemVo.SpuItemAttrGroupVo> vos = spuInfoService.allBaseAttr(16L, 225L);
        System.out.println(vos);
    }

    @Test
    public void getAllSaleAttrValue()
    {
        List<SkuItemVo.SkuItemSaleAttrVo> vos = skuInfoService.allSaleAttrValue(17L);
        System.out.println(vos);
    }
}
