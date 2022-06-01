package com.chen.gulimallproduct.service.impl;

import com.chen.gulimallproduct.dao.BrandDao;
import com.chen.gulimallproduct.dao.CategoryDao;
import com.chen.gulimallproduct.entity.BrandEntity;
import com.chen.gulimallproduct.vo.BrandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallproduct.dao.CategoryBrandRelationDao;
import com.chen.gulimallproduct.entity.CategoryBrandRelationEntity;
import com.chen.gulimallproduct.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    BrandDao brandDao;
    @Autowired
    CategoryDao categoryDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> queryByBrandId(Long brandId) {
        List<CategoryBrandRelationEntity> list = this.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
        );
        return list;
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        String brandName = brandDao.selectById(brandId).getName();
        String catelogName = categoryDao.selectById(catelogId).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(catelogName);
        this.save(categoryBrandRelation);
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> relations = this.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catId));
        List<BrandEntity> brands = new ArrayList<>();
        for(CategoryBrandRelationEntity entity:relations)
        {
            BrandEntity brand = brandDao.selectById(entity.getBrandId());
            brands.add(brand);
        }
        return brands;
    }

}