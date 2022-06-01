package com.chen.gulimallproduct.service.impl;

import com.chen.common.constant.ProductConstant;
import com.chen.common.to.SkuEsModel;
import com.chen.common.to.SkuReductionTo;
import com.chen.common.to.SpuBoundTo;
import com.chen.common.utils.R;
import com.chen.gulimallproduct.entity.*;
import com.chen.gulimallproduct.feign.CouponFeignService;
import com.chen.gulimallproduct.feign.SearchFeignService;
import com.chen.gulimallproduct.service.*;
import com.chen.gulimallproduct.vo.spusave.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallproduct.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //保存spu基本信息 pms_spu_info
        SpuInfoEntity info = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,info);
        info.setCreateTime(new Date());
        info.setUpdateTime(new Date());
        this.save(info);
        //保存spu描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(info.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.save(descEntity);
        //保存spu的图片集     pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(info.getId(),images);
        //保存spu规格参数   pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.saveBaseAttrs(info.getId(),baseAttrs);
        //保存spu的积分信息：sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(info.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()!=0)
        {
            log.error("远程保存spu积分信息失败");
        }
        //保存当前spu对应的所有sku信息：
        List<Skus> skus = vo.getSkus();
        //  sku的基本信息    pms_sku_info
        if(skus!=null&&skus.size()>0)
        {
            for(Skus sku:skus)
            {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setBrandId(info.getBrandId());
                skuInfoEntity.setCatalogId(info.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(info.getId());
                String defaultImg="";
                List<Images> skuImages = sku.getImages();
                for(Images skuImage:skuImages) {
                    if(skuImage.getDefaultImg()==1)
                        defaultImg = skuImage.getImgUrl();
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);
                //  sku的图片信息    pms_sku_images
                List<SkuImagesEntity> skuImagesEntityList = new ArrayList<>();
                for(Images skuImage:skuImages) {
                    // 没有图片路径的无需保存
                    if(!StringUtils.hasLength(skuImage.getImgUrl()))
                        continue;
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setDefaultImg(skuImage.getDefaultImg());
                    skuImagesEntity.setImgUrl(skuImage.getImgUrl());
                    skuImagesEntityList.add(skuImagesEntity);
                }
                skuImagesService.saveBatch(skuImagesEntityList);
                //sku的销售属性值   pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntityList = new ArrayList<>();
                for(Attr attr:attrs)
                {
                    SkuSaleAttrValueEntity saleAttrValue = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr,saleAttrValue);
                    saleAttrValue.setSkuId(skuInfoEntity.getSkuId());
                    saleAttrValueEntityList.add(saleAttrValue);
                }
                skuSaleAttrValueService.saveBatch(saleAttrValueEntityList);
                //  sku的优惠、满减等信息：sms_sku_ladder，sms_sku_full_reduction，sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1)
                {
                    couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r.getCode()!=0)
                    {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            }
        }
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //如果有关键字
        String key = (String)params.get("key");
        if(StringUtils.hasLength(key))
        {
            wrapper.and(new Consumer<QueryWrapper<SpuInfoEntity>>() {
                @Override
                public void accept(QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper) {
                    spuInfoEntityQueryWrapper.eq("id",key).or().like("spu_name",key)
                            .or().like("spu_description",key);
                }
            });
        }
        //如果有状态
        String status = (String)params.get("status");
        if(StringUtils.hasLength(status))
        {
            wrapper.eq("publish_status",status);
        }
        //如果有catelogId
        String catelogId = (String)params.get("catelogId");
        if(StringUtils.hasLength(catelogId))
        {
            wrapper.eq("catalog_id",catelogId);
        }
        //如果有brandId
        String brandId = (String)params.get("brandId");
        if(StringUtils.hasLength(brandId)&&!brandId.equals("0"))
        {
            wrapper.eq("brand_id",brandId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /*
    * 商品上架
    * */
    @Override
    public void up(Long spuId) {
        //TODO 先看看商品是不是已经上架了
        //首先要查出这个spuId对应的所有sku
        List<SkuInfoEntity> skuList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        //将数据库中的sku信息映射到SkuEsModel中
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        for(SkuInfoEntity skuInfo:skuList)
        {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfo,esModel);
            //skuPrice,skuImg,hasStock,hotScore,brandName,brandImg,catelogName,attrs
            //skuPrice
            esModel.setSkuPrice(skuInfo.getPrice());
            //skuImg
            esModel.setSkuImg(skuInfo.getSkuDefaultImg());
            //hotScore热度默认为0
            esModel.setHotScore(0L);
            //brandName,brandImg
            BrandEntity brand = brandService.getById(skuInfo.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            //catelogName,catelogId
            esModel.setCatelogId(skuInfo.getCatalogId());
            CategoryEntity category = categoryService.getById(skuInfo.getCatalogId());
            esModel.setCatelogName(category.getName());
            //hasStock
            //TODO 要通过调用仓储服务查
            esModel.setHasStock(true);
            //获得可检索的属性
            List<AttrEntity> searchableAttrs = new ArrayList<>();
            List<SkuEsModel.Attr> esAttrs = new ArrayList<>();
            //获得规格参数属性,将可检索的属性映射到ES模型
            List<ProductAttrValueEntity> spuAttrValues = productAttrValueService.list(
                    new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId)
            );
            for(ProductAttrValueEntity entity:spuAttrValues)
            {
                SkuEsModel.Attr esAttr = new SkuEsModel.Attr();
                AttrEntity byId = attrService.getById(entity.getAttrId());
                if(byId.getSearchType()==1)
                {
                    esAttr.setAttrId(entity.getAttrId());
                    esAttr.setAttrName(entity.getAttrName());
                    esAttr.setAttrValue(entity.getAttrValue());
                    esAttrs.add(esAttr);
                }
            }
            //获得销售属性,将可检索的属性映射到ES模型
            List<SkuSaleAttrValueEntity> skuSaleAttrValue = skuSaleAttrValueService.list(
                    new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id",skuInfo.getSkuId())
            );
            for(SkuSaleAttrValueEntity entity:skuSaleAttrValue)
            {
                SkuEsModel.Attr esAttr = new SkuEsModel.Attr();
                AttrEntity byId = attrService.getById(entity.getAttrId());
                if(byId.getSearchType()==1)
                {
                    esAttr.setAttrId(entity.getAttrId());
                    esAttr.setAttrName(entity.getAttrName());
                    esAttr.setAttrValue(entity.getAttrValue());
                    esAttrs.add(esAttr);
                }
            }
            esModel.setAttrs(esAttrs);
            skuEsModels.add(esModel);
        }
        //调用es接口上架sku
        R r = searchFeignService.batchsave(skuEsModels);
        if(r.getCode()==0){
            //上架成功，改变商品状态
            SpuInfoEntity spuInfo = new SpuInfoEntity();
            spuInfo.setId(spuId);
            spuInfo.setUpdateTime(new Date());
            spuInfo.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            this.updateById(spuInfo);
        }else {
            //远程调用失败
            //TODO 重复调用的问题？接口幂等性
        }

    }


}