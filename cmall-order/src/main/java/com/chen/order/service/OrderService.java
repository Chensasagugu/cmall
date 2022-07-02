package com.chen.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.order.entity.OrderEntity;
import com.chen.order.vo.OrderComfirmVo;
import com.chen.order.vo.OrderResponseVo;
import com.chen.order.vo.OrderSubmitVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:30:06
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderComfirmVo comfirmOrder() throws ExecutionException, InterruptedException;

    OrderResponseVo submitOrder(OrderSubmitVo submitVo);
}

