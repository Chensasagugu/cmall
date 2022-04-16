package com.chen.gulimallware.dao;

import com.chen.gulimallware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:40
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
