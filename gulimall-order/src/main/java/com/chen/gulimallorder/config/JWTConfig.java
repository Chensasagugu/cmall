package com.chen.gulimallorder.config;

import com.chen.common.utils.JwtUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chen
 * @date 2022.06.17 15:25
 */
@Configuration
public class JWTConfig {

    @Bean
    public JwtUtils jwtUtils()
    {
        return new JwtUtils();
    }

}
