package com.chen.gulimallproduct.vo.web;

import com.chen.gulimallproduct.entity.SkuImagesEntity;
import com.chen.gulimallproduct.entity.SkuInfoEntity;
import com.chen.gulimallproduct.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author chen
 * @date 2022.06.11 10:59
 */
@Data
public class SkuItemVo {
    //sku基本参数 pms_sku_info
    private SkuInfoEntity skuInfo;

    //sku的图片信息 pms_sku_images
    private List<SkuImagesEntity> images;

    //sku的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //获取spu的介绍 pms_spu_info_desc
    SpuInfoDescEntity desp;

    //spu规格参数
    private List<SpuItemAttrGroupVo>  groupAttrs;
    @ToString
    @Data
    public static class SkuItemSaleAttrVo
    {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

    @ToString
    @Data
    public static class SpuItemAttrGroupVo
    {
        private Long groupId;
        private String groupName;
        List<SpuBaseAttrVo> attrs;
    }

    @ToString
    @Data
    public static class SpuBaseAttrVo
    {
        private String attrName;
        private String attrValue;
    }
}
