package com.chen.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chen.authserver.feign")
@SpringBootApplication
public class CmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallAuthServerApplication.class, args);
    }

}
