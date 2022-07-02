package com.chen.gulimallorder.to;

import com.chen.gulimallorder.entity.OrderEntity;
import com.chen.gulimallorder.entity.OrderItemEntity;
import com.chen.gulimallorder.vo.LockStockVo;
import com.chen.gulimallorder.vo.OrderItemVo;
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
