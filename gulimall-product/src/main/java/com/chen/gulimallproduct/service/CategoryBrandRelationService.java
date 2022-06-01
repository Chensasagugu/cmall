package com.chen.gulimallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallproduct.entity.BrandEntity;
import com.chen.gulimallproduct.entity.CategoryBrandRelationEntity;
import com.chen.gulimallproduct.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> queryByBrandId(Long brandId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

