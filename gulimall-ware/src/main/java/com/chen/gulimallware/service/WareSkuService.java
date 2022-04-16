package com.chen.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallware.entity.WareSkuEntity;

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
}

