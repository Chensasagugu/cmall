package com.chen.cmallproduct.service.impl;

import com.chen.cmallproduct.entity.SkuImagesEntity;
import com.chen.cmallproduct.entity.SpuInfoDescEntity;
import com.chen.cmallproduct.service.SkuImagesService;
import com.chen.cmallproduct.service.SpuInfoDescService;
import com.chen.cmallproduct.service.SpuInfoService;
import com.chen.cmallproduct.vo.web.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.cmallproduct.dao.SkuInfoDao;
import com.chen.cmallproduct.entity.SkuInfoEntity;
import com.chen.cmallproduct.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Lazy
    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    ThreadPoolExecutor executor;
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

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo itemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = new CompletableFuture().supplyAsync(() -> {
            //sku基本信息
            SkuInfoEntity skuInfo = this.getById(skuId);
            itemVo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> imageFuture = new CompletableFuture().runAsync(() -> {
            //sku图片信息
            List<SkuImagesEntity> skuImages = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            itemVo.setImages(skuImages);
        }, executor);

        //spu的描述介绍
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            SpuInfoDescEntity spuDesc = spuInfoDescService.getById(res.getSpuId());
            itemVo.setDesp(spuDesc);
        }, executor);

        //spu的销售属性组合
        CompletableFuture<Void> saleAttrFuture =infoFuture.thenAcceptAsync((res)->{
            List<SkuItemVo.SkuItemSaleAttrVo> saleAttrVos = this.allSaleAttrValue(res.getSpuId());
            itemVo.setSaleAttr(saleAttrVos);
        },executor);

        //spu的基本属性规格参数
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SkuItemVo.SpuItemAttrGroupVo> spuItemAttrGroupVos = spuInfoService.allBaseAttr(res.getSpuId(), res.getCatalogId());
            itemVo.setGroupAttrs(spuItemAttrGroupVos);
        }, executor);
        CompletableFuture.allOf(imageFuture,descFuture,saleAttrFuture,baseAttrFuture).get();
        return itemVo;
    }

    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> allSaleAttrValue(Long spuId) {
        List<SkuItemVo.SkuItemSaleAttrVo> list = this.baseMapper.selectAllSaleAttrValue(spuId);
        return list;
    }

}