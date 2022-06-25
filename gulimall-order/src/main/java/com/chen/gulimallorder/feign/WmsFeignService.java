package com.chen.gulimallorder.feign;

import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author chen
 * @date 2022.06.23 10:17
 */
@FeignClient("gulimall-ware")
public interface WmsFeignService {

    @PostMapping("/gulimallware/waresku/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

    /***
     * 获得运费
     * @param addrId
     * @return
     */
    @GetMapping("/gulimallware/wareinfo/fare")
    public R getFare(@RequestParam("addrId") Long addrId);
}