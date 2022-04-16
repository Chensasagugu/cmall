package com.chen.gulimallproduct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.gulimallproduct.entity.BrandEntity;
import com.chen.gulimallproduct.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {
        /*
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        */
        /*
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setName("苹果");
        brandService.updateById(brandEntity);
         */

        //查询
        List<BrandEntity> list=brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L));
        list.forEach((item)->{
            System.out.println(item);
        });
    }

}
