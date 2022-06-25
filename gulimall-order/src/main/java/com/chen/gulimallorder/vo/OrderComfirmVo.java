package com.chen.gulimallorder.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author chen
 * @date 2022.06.21 15:25
 */
//@Data
public class OrderComfirmVo {

    //收货地址 ums_member_recieve_address表
    @Getter @Setter
    List<MemberAddressVo> address;

    //所有选中的购物项
    @Getter @Setter
    List<OrderItemVo> items;

    @Getter @Setter
    Map<Long,Boolean> skuHasStock;

    //发票记录..

    //用户积分
    @Getter @Setter
    private Integer integration;

    //防重令牌
    @Getter @Setter
    private String orderToken;




    public BigDecimal getTotol()
    {
        BigDecimal sum = new BigDecimal("0");
        for(OrderItemVo itemVo:items)
        {
            BigDecimal itemPrice = itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString()));
            sum = sum.add(itemPrice);
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotol();
    }


}
