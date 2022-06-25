package com.chen.gulimallorder.feign;

import com.chen.gulimallorder.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @GetMapping("/gulimallmember/memberreceiveaddress/{memberId}/addresses")
    public List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);

    @GetMapping("/gulimallmember/member/{memberId}/integration")
    public Integer getIntegration(@PathVariable("memberId") Long memberId);
}
