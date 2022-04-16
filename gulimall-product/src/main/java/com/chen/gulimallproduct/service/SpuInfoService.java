package com.chen.gulimallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallproduct.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
