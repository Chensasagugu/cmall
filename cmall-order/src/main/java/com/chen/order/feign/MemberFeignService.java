package com.chen.order.feign;

import com.chen.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("cmall-member")
public interface MemberFeignService {

    @GetMapping("/gulimallmember/memberreceiveaddress/{memberId}/addresses")
    public List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);

    @GetMapping("/gulimallmember/member/{memberId}/integration")
    public Integer getIntegration(@PathVariable("memberId") Long memberId);
}
