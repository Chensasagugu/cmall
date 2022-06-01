package com.chen.gulimallsearch.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索条件
 * @author chen
 * @date 2022.05.30 14:11
 */
@Data
@ToString
public class SearchParam {
    //关键字
    private String keyword;
    //分类Id
    private Long catalogId;
    //品牌Id
    private List<Long> brandId;
    //价格区间 最小价格-最大价格
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    /**
     * 排序规则：
     * 1.默认排序
     * 2.销量排序 saleCount_asc/desc
     * 3.价格排序 price_asc/desc
     * 4.评分排序 score_asc/desc
     * 5.上架时间排序 upTime_asc/desc
     */
    private String sortRule;

    /**
     * 属性
     */
    private List<String> attrs;

    //是否有货
    private Integer hasStock;
    //分页号
    private Integer page;

}
