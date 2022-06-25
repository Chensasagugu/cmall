package com.chen.gulimallorder.controller;

import com.chen.gulimallorder.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author chen
 * @date 2022.06.15 15:21
 */
@RestController
public class RabbitController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMsg")
    public void sendMessage()
    {
        OrderReturnReasonEntity en = new OrderReturnReasonEntity();
        en.setId(1L);
        en.setCreateTime(new Date());
        en.setName("受打击");
        String msg = "Hello World";
        rabbitTemplate.convertAndSend("hello.java-exchange","hello.java",en,new CorrelationData(UUID.randomUUID().toString()));
    }
}
