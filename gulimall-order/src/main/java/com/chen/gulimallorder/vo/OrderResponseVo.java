package com.chen.gulimallorder.vo;

import com.chen.gulimallorder.entity.OrderEntity;
import lombok.Data;

/**
 * @author chen
 * @date 2022.06.24 13:36
 */
@Data
public class OrderResponseVo {

    private OrderEntity order;
    private Integer code;
}
