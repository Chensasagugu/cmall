package com.chen.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.chen.order.dao")
public class CmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallOrderApplication.class, args);
    }

}
