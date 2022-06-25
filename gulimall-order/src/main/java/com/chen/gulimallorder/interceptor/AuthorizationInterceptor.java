package com.chen.gulimallorder.interceptor;

import com.chen.common.annotation.Login;
import com.chen.common.exception.RRException;
import com.chen.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chen
 * @date 2022.06.17 15:19
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    public static ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Login annotation;
        if(handler instanceof HandlerMethod)
        {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        }else {
            return true;
        }
        //如果方法不带Login，则不需要鉴权
        if(annotation==null)
        {
            return true;
        }
        //开始鉴权
        String token = request.getHeader(jwtUtils.getHeader());
        if(!StringUtils.hasLength(token))
            token = request.getParameter(jwtUtils.getHeader());
        //凭证为空
        if(org.apache.commons.lang.StringUtils.isBlank(token)){
            throw new RRException(jwtUtils.getHeader() + "不能为空", HttpStatus.UNAUTHORIZED.value());
        }

        //是否过期
        Claims claims = jwtUtils.getClaimByToken(token);
        if(claims == null || jwtUtils.isTokenExpired(claims.getExpiration())){
            throw new RRException(jwtUtils.getHeader() + "失效或不合法，请重新登录", HttpStatus.UNAUTHORIZED.value());
        }
        userIdThreadLocal.set(Long.parseLong(claims.getSubject()));
        return true;
    }
}
