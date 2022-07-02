package com.chen.gulimallorder.design.discount;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface IDiscount<T> {

    public BigDecimal discountAmount(T discountInfo,BigDecimal skuPrice);
}
