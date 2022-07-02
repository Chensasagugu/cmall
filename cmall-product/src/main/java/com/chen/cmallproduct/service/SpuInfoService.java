package com.chen.cmallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.cmallproduct.entity.SpuInfoEntity;
import com.chen.cmallproduct.vo.spusave.SpuSaveVo;
import com.chen.cmallproduct.vo.web.SkuItemVo;

import java.util.List;
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

    void saveSpuInfo(SpuSaveVo vo);


    PageUtils queryPageByCondition(Map<String, Object> params);

    //商品上架
    void up(Long spuId);

    // 获得商品的所有基本属性
    List<SkuItemVo.SpuItemAttrGroupVo> allBaseAttr(Long spuId,Long catalogId);

    SpuInfoEntity getBySkuId(Long skuId);
}

