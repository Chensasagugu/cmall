package com.chen.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chen
 * @date 2022.06.23 17:25
 */
@Data
public class OrderSubmitVo {
    //收货地址id
    private Long addrId;

    //支付方法
    private Integer payType;

    //无需提交要购买的商品，去购物车再获取一编
    //优惠，发票

    //订单令牌
    private String orderToken;
    //应付价格
    private BigDecimal payPrice;

    //订单备注
    private String note;
}
