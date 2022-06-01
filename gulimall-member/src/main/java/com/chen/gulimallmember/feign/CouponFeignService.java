package com.chen.gulimallmember.feign;

import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chen
 * @date 2022.04.16 10:44
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("/gulimallcoupon/coupon/getAcoupon")
    public R oneCoupon();
}
