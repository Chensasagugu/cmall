package com.chen.gulimallauthserver.feign;

import com.chen.common.to.UserLoginTo;
import com.chen.common.utils.R;
import com.chen.common.valid.login.LoginGroup;
import com.chen.common.valid.login.RegisterGroup;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chen
 * @date 2022.06.16 14:59
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @RequestMapping("/gulimallmember/member/register")
    public R register(UserLoginTo userLoginTo);

    @RequestMapping("/gulimallmember/member/login")
    public R login(UserLoginTo userLoginTo);

}
