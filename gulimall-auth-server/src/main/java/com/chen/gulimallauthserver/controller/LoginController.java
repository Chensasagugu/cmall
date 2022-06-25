package com.chen.gulimallauthserver.controller;

import com.chen.common.to.UserLoginTo;
import com.chen.common.utils.JwtUtils;
import com.chen.common.utils.R;
import com.chen.common.valid.login.LoginGroup;
import com.chen.common.valid.login.RegisterGroup;
import com.chen.gulimallauthserver.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author chen
 * @date 2022.05.14 12:05
 */
@RestController
public class LoginController {
    @Autowired
    LoginService loginService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/getCode")
    public R getCode(@RequestParam("phone") String phone)
    {
        String code = loginService.getVerifyCode(phone);
        return R.ok().put("data",code);
    }

    @PostMapping("/register")
    public R register(@Validated(RegisterGroup.class) @RequestBody UserLoginTo userTo)
    {
        loginService.register(userTo);
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@Validated(LoginGroup.class) @RequestBody UserLoginTo userTo)
    {
        String token = loginService.login(userTo);
        return R.ok().put(jwtUtils.getHeader(),token);
    }
}
