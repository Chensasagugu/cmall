package com.chen.gulimallorder.feign;

import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据skuId获得spu信息
     */
    @GetMapping("/gulimallproduct/spuinfo/infoBySkuId/{skuId}")
    public R getInfoBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 获得品牌名
     * @param brandId
     * @return
     */
    @GetMapping("/gulimallproduct/brand/brandName/{brandId}")
    public String brandName(@PathVariable("brandId") Long brandId);
}
