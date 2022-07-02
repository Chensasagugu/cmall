package com.chen.order.mq;

import com.chen.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chen
 * @date 2022.06.28 14:48
 */
@Configuration
public class MyMQConfig {
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    public static final String ORDER_RELEASE_QUEUE = "order.release.queue";

    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";

    public static final String EXCHANGE_DELAY_ROUTING_KEY = "order.create";

    public static final String EXCHANGE_RELEASE_ROUTING_KEY = "order.release";

    //@RabbitListener(queues = {ORDER_RELEASE_QUEUE})
    public void Comsumer(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("接收到过期order"+order);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
    @Bean
    public Queue orderDelayQueue()
    {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",ORDER_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key",EXCHANGE_RELEASE_ROUTING_KEY);
        arguments.put("x-message-ttl",60000);
        Queue queue = new Queue(ORDER_DELAY_QUEUE,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseQueue()
    {
        return new Queue(ORDER_RELEASE_QUEUE,true,false,false);
    }

    @Bean
    public Exchange orderEventExchange()
    {
        return new TopicExchange(ORDER_EVENT_EXCHANGE,true,false);
    }

    @Bean
    public Binding orderCreateBinding()
    {
        return new Binding(ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,ORDER_EVENT_EXCHANGE,
                EXCHANGE_DELAY_ROUTING_KEY,
                null);
    }

    @Bean
    public Binding orderReleaseBinding()
    {
        return new Binding(ORDER_RELEASE_QUEUE,
                Binding.DestinationType.QUEUE,ORDER_EVENT_EXCHANGE,
                EXCHANGE_RELEASE_ROUTING_KEY,
                null);
    }
}
