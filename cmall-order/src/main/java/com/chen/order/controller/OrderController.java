package com.chen.order.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.chen.common.annotation.Login;
import com.chen.order.mq.MyMQConfig;
import com.chen.order.vo.OrderComfirmVo;
import com.chen.order.vo.OrderResponseVo;
import com.chen.order.vo.OrderSubmitVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chen.order.entity.OrderEntity;
import com.chen.order.service.OrderService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 订单
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:30:06
 */
@RestController
@RequestMapping("gulimallorder/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendTest")
    public R sendTest()
    {
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(MyMQConfig.ORDER_EVENT_EXCHANGE,"order.create",entity);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallorder:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimallorder:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallorder:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallorder:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallorder:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /***
     * 确认订单数据
     * @return
     */
    @Login
    @GetMapping("/comfirm")
    public R comfirmOrder() throws ExecutionException, InterruptedException {
        OrderComfirmVo comfirmVo = orderService.comfirmOrder();
        return R.ok().put("data",comfirmVo);
    }

    @Login
    @PostMapping("/submitOrder")
    public R submitOrder(@RequestBody OrderSubmitVo submitVo)
    {
        OrderResponseVo order = orderService.submitOrder(submitVo);
        if(order.getCode()==OrderResponseVo.ResponseCode.SUCCESS.getCode())
            return R.ok().setData(order);
        else if(order.getCode()==OrderResponseVo.ResponseCode.TOKEN_VALIDATION_FAIL.getCode())
            return R.error(OrderResponseVo.ResponseCode.TOKEN_VALIDATION_FAIL.getMsg());
        else if(order.getCode()==OrderResponseVo.ResponseCode.PRICE_VALIDATION_FAIL.getCode())
            return R.error(OrderResponseVo.ResponseCode.PRICE_VALIDATION_FAIL.getMsg());
        else if(order.getCode()==OrderResponseVo.ResponseCode.LOCK_STOCK_FAIL.getCode())
            return R.error(OrderResponseVo.ResponseCode.LOCK_STOCK_FAIL.getMsg());
        else
            return R.error("未知错误");
    }
}
