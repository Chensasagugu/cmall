package com.chen.ware.vo;

import lombok.Data;

/**
 * @author chen
 * @date 2022.06.26 10:55
 */
@Data
public class LockStockVo {

    //订单id
    private String orderSn;

    private Long skuId;

    private Integer lockCount;
}
