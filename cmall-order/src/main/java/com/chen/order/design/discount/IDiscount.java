package com.chen.order.design.discount;

import java.math.BigDecimal;


public interface IDiscount<T> {

    public BigDecimal discountAmount(T discountInfo,BigDecimal skuPrice);
}
