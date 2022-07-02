package com.chen.order.design.discount.impl;

import com.chen.order.design.discount.IDiscount;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author chen
 * @date 2022.06.25 21:30
 */
public class MJDiscount implements IDiscount<Map<String,String>> {
    public static final String REACH_PRICE = "reachPrice";
    public static final String DISCOUNT_PRICE = "discountPrice";
    @Override
    public BigDecimal discountAmount(Map<String, String> discountInfo, BigDecimal skuPrice) {
        String reachPrice = discountInfo.get(REACH_PRICE);
        String discountPrice = discountInfo.get(DISCOUNT_PRICE);
        if(skuPrice.compareTo(new BigDecimal(reachPrice))<0)
            return skuPrice;
        BigDecimal discountAmount = skuPrice.subtract(new BigDecimal(discountPrice));
        if(discountAmount.compareTo(BigDecimal.ZERO)<1)
            return BigDecimal.ONE;
        return discountAmount;
    }
}
