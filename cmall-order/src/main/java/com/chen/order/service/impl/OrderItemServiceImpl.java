package com.chen.order.service.impl;

import com.chen.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.order.dao.OrderItemDao;
import com.chen.order.entity.OrderItemEntity;
import com.chen.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    //@RabbitListener(queues = {"hello.java-queue"})
    public void recieveMessage(Message mesage,
                               OrderReturnReasonEntity entity,
                               Channel channel) throws IOException {
        System.out.println("接受到消息...内容"+entity+"==>类型"+mesage.getClass());
        long deliveryTag = mesage.getMessageProperties().getDeliveryTag();
        //channel.basicNack(deliveryTag,false,true);
    }
}