package com.chen.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:41
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

