package com.chen.gulimallauthserver.config;

import com.chen.common.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chen
 * @date 2022.06.17 14:22
 */
@Configuration
public class JWTConfig {

    @Bean
    JwtUtils jwtUtils()
    {
        return new JwtUtils();
    }
}
