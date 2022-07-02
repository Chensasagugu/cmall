package com.chen.gulimallware.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chen
 * @date 2022.06.29 12:14
 */
@Configuration
public class MyMQConfig {

    //交换机
    public static final String STOCK_EVENT_EXCHANGE = "stock-event-exchange";

    public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";

    public static final String STOCK_RELEASE_QUEUE = "stock.release.queue";

    public static final String EXCHANGE_DELAY_ROUTING_KEY = "stock.locked";

    public static final String EXCHANGE_RELEASE_ROUTING_KEY = "stock.release";

    @Bean
    public Queue stockDelayQueue()
    {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",STOCK_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key",EXCHANGE_RELEASE_ROUTING_KEY);
        arguments.put("x-message-ttl",120000);
        Queue queue = new Queue(STOCK_DELAY_QUEUE,true,false,false,arguments);
        return queue;
    }

    @Bean
    public Queue stockReleaseQueue()
    {
        return new Queue(STOCK_RELEASE_QUEUE,true,false,false);
    }

    @Bean
    public Exchange stockEventExchange()
    {
        return new TopicExchange(STOCK_EVENT_EXCHANGE,true,false);
    }

    @Bean
    public Binding stockLockedBinding()
    {
        return new Binding(STOCK_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,STOCK_EVENT_EXCHANGE,
                EXCHANGE_DELAY_ROUTING_KEY,
                null);
    }

    @Bean
    public Binding stockReleaseBinding()
    {
        return new Binding(STOCK_RELEASE_QUEUE,
                Binding.DestinationType.QUEUE,STOCK_EVENT_EXCHANGE,
                EXCHANGE_RELEASE_ROUTING_KEY,
                null);
    }
}
