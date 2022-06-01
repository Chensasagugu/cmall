package com.chen.gulimallproduct.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallproduct.dao.SkuInfoDao;
import com.chen.gulimallproduct.entity.SkuInfoEntity;
import com.chen.gulimallproduct.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        //有关键字
        String key = (String)params.get("key");
        if(StringUtils.hasLength(key))
        {
            wrapper.and(new Consumer<QueryWrapper<SkuInfoEntity>>() {
                @Override
                public void accept(QueryWrapper<SkuInfoEntity> skuInfoEntityQueryWrapper) {
                    skuInfoEntityQueryWrapper.eq("sku_id",key).or().like("sku_name",key);
                }
            });
        }
        //有分类id
        String catelogId = (String)params.get("catelogId");
        if(StringUtils.hasLength(catelogId)&&!"0".equals(catelogId))
        {
            wrapper.eq("catalog_id",catelogId);
        }
        //有品牌id
        String brandId = (String)params.get("brandId");
        if(StringUtils.hasLength(brandId)&&!"0".equalsIgnoreCase(brandId))
        {
            wrapper.eq("brand_id",brandId);
        }
        //有最小值
        String min = (String)params.get("min");
        if(StringUtils.hasLength(min))
        {
            wrapper.ge("price",min);
        }
        //有最大值
        String max = (String)params.get("max");
        if(StringUtils.hasLength(max))
        {
            BigDecimal decimal = new BigDecimal(max);
            if(decimal.compareTo(new BigDecimal("0"))==1)
                wrapper.le("price",max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}