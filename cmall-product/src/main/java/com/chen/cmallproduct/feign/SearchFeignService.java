package com.chen.cmallproduct.feign;

import com.chen.common.to.SkuEsModel;
import com.chen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("cmall-search")
public interface SearchFeignService {

    @RequestMapping("/esproduct/batchsave")
    public R batchsave(@RequestBody List<SkuEsModel> esModels);
}
