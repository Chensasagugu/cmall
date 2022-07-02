package com.chen.cart.feign;

import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@FeignClient("cmall-product")
public interface ProductFeignService {

    @RequestMapping("/gulimallproduct/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    @RequestMapping("/gulimallproduct/skusaleattrvalue/simpleInfo/{id}")
    public R simpleInfo(@PathVariable("id") Long id);

    @GetMapping("/gulimallproduct/skuinfo/{skuId}/price")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);
}
