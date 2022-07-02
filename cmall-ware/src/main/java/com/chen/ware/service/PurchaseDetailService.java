package com.chen.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:41
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

