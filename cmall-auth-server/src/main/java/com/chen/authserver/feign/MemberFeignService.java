package com.chen.authserver.feign;

import com.chen.common.to.UserLoginTo;
import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chen
 * @date 2022.06.16 14:59
 */
@FeignClient("cmall-member")
public interface MemberFeignService {

    @RequestMapping("/gulimallmember/member/register")
    public R register(UserLoginTo userLoginTo);

    @RequestMapping("/gulimallmember/member/login")
    public R login(UserLoginTo userLoginTo);

}
