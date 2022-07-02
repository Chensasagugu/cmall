package com.chen.cmallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.cmallproduct.entity.CategoryEntity;
import com.chen.cmallproduct.vo.web.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listTree();

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到catelogId的完整路径
     * [父/子/孙]
     */
    Long[] findCatelogPath(Long catelogId);

    void updateRelativeColomn(Long catId, String name);

    List<CategoryEntity> getLevel1Categories();

    Map<String,List<Catalog2Vo>> getCatelogJSON();
}

