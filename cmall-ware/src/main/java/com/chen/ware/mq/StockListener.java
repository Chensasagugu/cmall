package com.chen.ware.mq;

import com.chen.ware.vo.LockStockVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author chen
 * @date 2022.06.29 12:17
 */
@Component
@RabbitListener(queues = {MyMQConfig.STOCK_RELEASE_QUEUE})
public class StockListener {

    @RabbitHandler
    public void handleReleaseStock(Message message,
                                   List<LockStockVo> lockStockVos,
                                   Channel channel) throws IOException {
        System.out.println("接收到延时消息："+lockStockVos);
        //查看订单状态

        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
