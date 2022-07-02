package com.chen.authserver.service.impl;

import com.chen.common.exception.BizCodeEnum;
import com.chen.common.to.UserLoginTo;
import com.chen.common.utils.JwtUtils;
import com.chen.common.utils.R;
import com.chen.authserver.constant.AuthServerConstant;
import com.chen.authserver.exception.LoginFailException;
import com.chen.authserver.exception.RegisterFailException;
import com.chen.authserver.exception.VerifyCodeException;
import com.chen.authserver.feign.MemberFeignService;
import com.chen.authserver.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 * @date 2022.06.16 13:04
 */
@Service("LoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public String getVerifyCode(String phone) {
        //检查该手机是否已经获得过验证码，验证码防刷
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone);
        if(StringUtils.hasLength(s))
            throw new VerifyCodeException(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
        String code = UUID.randomUUID().toString().substring(0,6);
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code,3, TimeUnit.MINUTES);
        return code;
    }

    @Override
    public void register(UserLoginTo userLoginTo) {
        //验证码是否正确
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX+userLoginTo.getPhone());
        if(!StringUtils.hasLength(s)||!s.equals(userLoginTo.getCode()))
            throw new VerifyCodeException(BizCodeEnum.SMS_CODE_WRONG_EXCEPTION.getCode()
                    ,BizCodeEnum.SMS_CODE_WRONG_EXCEPTION.getMsg());
        //远程调用注册接口
        R r = memberFeignService.register(userLoginTo);
        if(r.getCode()==0)
        {
            //注册成功
            //删除缓存中的验证码
            stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX+userLoginTo.getPhone());
        }else {
            //注册失败
            throw new RegisterFailException(r.getCode(), (String) r.get("msg"));
        }
    }

    @Override
    public String login(UserLoginTo userVo) {
        R r = memberFeignService.login(userVo);
        if(r.getCode()==0)
        {
            //登录成功
            //创建JWT
            int userId = (int) r.get("data");
            String token = jwtUtils.generateToken(userId);
            return token;
        }else {
            //登录失败
            throw new LoginFailException(r.getCode(),r.getMessage());
        }
    }
}
