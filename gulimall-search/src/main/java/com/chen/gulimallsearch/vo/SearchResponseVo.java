package com.chen.gulimallsearch.vo;

import com.chen.common.to.SkuEsModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author chen
 * @date 2022.05.30 14:42
 */
@ToString
@Data
public class SearchResponseVo {
    //当前分页商品列表
    private List<SkuEsModel> products;

    //当前分页号
    private Integer currentPage;

    //总页数
    private Integer totalPage;

    //总记录数
    private Long total;

    //当前分类下其他品牌
    private List<BrandVo> brands;
    //分类下的子分类
    private List<CatalogVo> catalogs;
    //所有属性以及可选值
    private List<AttrVo> attrs;
    @Data
    public static class BrandVo{
        Long brandId;
        String brandName;
        String brandLogo;
    }
    @Data
    public static class AttrVo{
        Long attrId;
        String attrName;
        List<String> attrValue;
    }
    @Data
    public static class CatalogVo{
        Long catalogId;
        String catalogName;
    }
}
