package com.chen.gulimallware.dao;

import com.chen.gulimallware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.gulimallware.vo.SkuHasStockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:40
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<SkuHasStockVo> getSkuHasStock(@Param("skuIds") List<Long> skuIds);
}
