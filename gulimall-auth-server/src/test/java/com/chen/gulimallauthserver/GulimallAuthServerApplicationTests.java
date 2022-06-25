package com.chen.gulimallauthserver;

import com.chen.common.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallAuthServerApplicationTests {

    @Autowired
    JwtUtils jwtUtils;
    @Test
    void contextLoads() {
        System.out.println(jwtUtils);
    }

}
