package com.chen.gulimallware;

import com.chen.gulimallware.dao.WareSkuDao;
import com.chen.gulimallware.vo.SkuHasStockVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GulimallWareApplicationTests {

    @Autowired
    WareSkuDao wareSkuDao;
    @Test
    void contextLoads() {
        List<Long> skuIds = new ArrayList<>();
        skuIds.add(20L);
        skuIds.add(21L);
        skuIds.add(23L);
        skuIds.add(25L);
        List<SkuHasStockVo> skuHasStock = wareSkuDao.getSkuHasStock(skuIds);
        System.out.println(skuHasStock);
    }

}
