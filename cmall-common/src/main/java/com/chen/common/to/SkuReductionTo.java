package com.chen.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chen
 * @date 2022.05.03 15:06
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
