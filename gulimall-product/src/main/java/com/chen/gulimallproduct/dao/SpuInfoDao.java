package com.chen.gulimallproduct.dao;

import com.chen.gulimallproduct.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.gulimallproduct.vo.web.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu信息
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> selectALLBaseAttr(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
