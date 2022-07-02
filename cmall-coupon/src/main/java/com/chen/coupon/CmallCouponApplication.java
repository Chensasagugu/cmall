package com.chen.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.chen.coupon.dao")
public class CmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallCouponApplication.class, args);
    }

}
