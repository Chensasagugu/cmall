package com.chen.gulimallcart.config;

import com.chen.common.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chen
 * @date 2022.06.19 17:17
 */
@Configuration
public class JwtConfig {

    @Bean
    public JwtUtils jwtUtils()
    {
        return new JwtUtils();
    }
}
