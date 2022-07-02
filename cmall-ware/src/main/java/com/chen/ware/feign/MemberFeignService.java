package com.chen.ware.feign;

import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("cmall-member")
public interface MemberFeignService {

    @RequestMapping("/gulimallmember/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
