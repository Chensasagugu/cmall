package com.chen.gulimallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallproduct.entity.AttrEntity;
import com.chen.gulimallproduct.vo.AttrRespVo;
import com.chen.gulimallproduct.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

}

