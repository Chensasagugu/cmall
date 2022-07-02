package com.chen.ware;

import com.chen.ware.dao.WareSkuDao;
import com.chen.ware.vo.LockStockVo;
import com.chen.ware.vo.SkuHasStockVo;
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
    @Test
    void testWare() {
        LockStockVo lockInfo = new LockStockVo();
        lockInfo.setLockCount(2);
        lockInfo.setSkuId(20L);
        List<Long> wareIds = wareSkuDao.getWareHashStock(lockInfo);
        System.out.println(wareIds);
    }

    @Test
    void testWareLock() {
        LockStockVo lockInfo = new LockStockVo();
        lockInfo.setLockCount(2);
        lockInfo.setSkuId(25L);
        Integer rows = wareSkuDao.lockWareStock(1L,lockInfo);
        System.out.println(rows);
    }
}
