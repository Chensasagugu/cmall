package com.chen.order.to;

import com.chen.order.entity.OrderEntity;
import com.chen.order.entity.OrderItemEntity;
import com.chen.order.vo.LockStockVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chen
 * @date 2022.06.24 13:45
 */
@Data
public class OrderCreateTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private List<LockStockVo> itemLocks;

    private BigDecimal payPrice;

    //运费
    private BigDecimal fare;
}
