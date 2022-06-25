package com.chen.gulimallorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallorder.entity.OrderEntity;
import com.chen.gulimallorder.vo.OrderComfirmVo;
import com.chen.gulimallorder.vo.OrderResponseVo;
import com.chen.gulimallorder.vo.OrderSubmitVo;

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

