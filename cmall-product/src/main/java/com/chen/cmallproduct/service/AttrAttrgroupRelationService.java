package com.chen.cmallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.cmallproduct.entity.AttrAttrgroupRelationEntity;
import com.chen.cmallproduct.entity.AttrEntity;
import com.chen.cmallproduct.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<AttrEntity> getAttrList(Long attrGroupId);

    void deleteItems(AttrGroupRelationVo[] vos);

    PageUtils getNoAttrList(Map<String, Object> params, Long attrGroupId);

    void saveBatch(List<AttrGroupRelationVo> vos);
}

