package com.chen.gulimallproduct.dao;

import com.chen.gulimallproduct.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    /***
     * 查询销售属性以及值的列表，返回值格式：[{attr_name}:{attr_value}]
     * @param id
     */
    List<String> selectSimpleInfoById(@Param("id") Long id);
}
