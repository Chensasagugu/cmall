package com.chen.order.mq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.order.entity.OrderEntity;
import com.chen.order.enume.OrderStatusEnum;
import com.chen.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author chen
 * @date 2022.06.29 11:15
 */
@Component
@RabbitListener(queues = {MyMQConfig.ORDER_RELEASE_QUEUE})
public class OrderListener {


    @Autowired
    OrderService orderService;

    /**
     * 处理延时队列中的订单
     * @param message
     * @param order
     * @param channel
     */
    @RabbitHandler
    public void handleReleaseOrder(Message message,
                                   OrderEntity order,
                                   Channel channel) throws IOException {
        System.out.println("收到延时队列中的订单："+order);
        //查看订单状态
        Integer status = order.getStatus();
        if(status== OrderStatusEnum.CREATE_NEW.getCode()||status== OrderStatusEnum.CANCLED.getCode())
        {
            //用户没有付款或者取消订单
            //把订单取消
            System.out.println("用户并没有付款");
            OrderEntity entity = new OrderEntity();
            entity.setStatus(OrderStatusEnum.CANCLED.getCode());
            entity.setModifyTime(new Date());
            orderService.update(entity,new QueryWrapper<OrderEntity>().eq("id",order.getId()));
        }else{
            //用户已经完成支付

        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
