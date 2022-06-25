package com.chen.gulimallauthserver.service;

import com.chen.common.to.UserLoginTo;
import com.chen.gulimallauthserver.exception.VerifyCodeException;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    //获得验证码
    String getVerifyCode(String phone) throws VerifyCodeException;
    //注册
    void register(UserLoginTo userVo);
    //登录
    String login(UserLoginTo userVo);
}
