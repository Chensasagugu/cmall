package com.chen.order.feign;

import com.chen.common.utils.R;
import com.chen.order.vo.LockStockVo;
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
@FeignClient("cmall-ware")
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

    /***
     * 锁库存
     * @param lockStockVos
     * @return
     */
    @PostMapping("/gulimallware/waresku/lockStock")
    public R lockSkuStock(@RequestBody List<LockStockVo> lockStockVos);
}
