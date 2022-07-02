package com.chen.cmallproduct.feign;

import com.chen.common.to.SkuReductionTo;
import com.chen.common.to.SpuBoundTo;
import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author chen
 * @date 2022.05.03 14:49
 */
@FeignClient("cmall-coupon")
public interface CouponFeignService {

    /**
     * 1、CouponFeignService.saveSpuBounds(spuBoundTo)
     *      1)、@RequestBody将这个对象转化为json
     *      2）、找到gulimall-coupon服务，给/gulimallcoupon/spubounds/save发送请求
     *      3）、对方服务收到请求，请求体里有json数据。
     *          将请求体中的json转为（@RequestBody SpuBoundsEntity spuBounds）
     * 只要json数据模型是兼容的。双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/gulimallcoupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/gulimallcoupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
