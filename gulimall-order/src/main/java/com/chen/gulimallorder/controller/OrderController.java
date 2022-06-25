package com.chen.gulimallorder.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.chen.common.annotation.Login;
import com.chen.gulimallorder.vo.OrderComfirmVo;
import com.chen.gulimallorder.vo.OrderResponseVo;
import com.chen.gulimallorder.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chen.gulimallorder.entity.OrderEntity;
import com.chen.gulimallorder.service.OrderService;
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
        return R.ok().setData(order);
    }
}
