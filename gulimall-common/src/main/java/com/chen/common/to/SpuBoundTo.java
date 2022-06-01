package com.chen.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chen
 * @date 2022.05.03 14:53
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
