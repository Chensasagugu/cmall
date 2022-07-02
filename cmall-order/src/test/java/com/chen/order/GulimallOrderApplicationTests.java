package com.chen.order;

import com.chen.order.entity.OrderReturnReasonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void createExchange() {
        DirectExchange directExchange = new DirectExchange("hello.java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
    }
    @Test
    void createQueue() {
        Queue queue = new Queue("hello.java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
    }

    @Test
    void createBinding(){
        Binding binding = new Binding("hello.java-queue", Binding.DestinationType.QUEUE,
                "hello.java-exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
    }

    /*
    * 发消息
    * */
    @Test
    void sendMessage(){
        OrderReturnReasonEntity en = new OrderReturnReasonEntity();
        en.setId(1L);
        en.setCreateTime(new Date());
        en.setName("受打击");
        String msg = "Hello World";
        rabbitTemplate.convertAndSend("hello.java-exchange","hello.java",en);
    }
}
