package com.chen.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallware.entity.WareSkuEntity;
import com.chen.gulimallware.vo.LockStockVo;
import com.chen.gulimallware.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    boolean lockStock(List<LockStockVo> lockStockVos);
}

