package com.chen.gulimallorder.config;

import com.chen.common.utils.JwtUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chen
 * @date 2022.06.22 14:19
 */
@Configuration
public class MyFeignConfig {
    @Autowired
    JwtUtils jwtUtils;

    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //RequestContextHolder可以拿到request数据，底层用ThreadLocal实现
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                //RequestContextHolder.setRequestAttributes(requestAttributes,true);
                HttpServletRequest request = requestAttributes.getRequest();
                if (request!=null)
                {
                    String token = request.getHeader(jwtUtils.getHeader());
                    if(StringUtils.hasLength(token))
                    {
                        requestTemplate.header(jwtUtils.getHeader(),token);
                    }
                }
            }
        };
    }
}
