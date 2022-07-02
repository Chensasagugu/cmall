package com.chen.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chen
 * @date 2022.06.21 15:27
 */
@Data
public class OrderItemVo {

    private Long skuId;
    private String title;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private String image;

    //库存状态
    private boolean hasStock;
    private BigDecimal weight;
}
