package com.chen.cmallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.cmallproduct.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<String> getSimpleInfoById(Long id);
}

