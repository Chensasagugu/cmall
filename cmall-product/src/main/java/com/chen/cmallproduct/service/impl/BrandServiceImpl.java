package com.chen.cmallproduct.service.impl;

import com.chen.cmallproduct.dao.CategoryBrandRelationDao;
import com.chen.cmallproduct.entity.CategoryBrandRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.cmallproduct.dao.BrandDao;
import com.chen.cmallproduct.entity.BrandEntity;
import com.chen.cmallproduct.service.BrandService;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if(StringUtils.hasLength(key))
        {
            queryWrapper.eq("brand_id",key).or().like("name",key);

        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateRelativeColomn(Long brandId, String name) {
        //更新pms_category_brand_relation表中的品牌名
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setBrandId(brandId);
        entity.setBrandName(name);
        categoryBrandRelationDao.update(entity,
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
        //TODO 更新其他关联表中的品牌名
    }

}