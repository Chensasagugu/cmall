package com.chen.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chen.member.feign")
@MapperScan("com.chen.member.dao")
public class CmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallMemberApplication.class, args);
    }

}
