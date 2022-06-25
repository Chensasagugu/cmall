package com.chen.gulimallproduct.web;

import com.chen.gulimallproduct.service.SkuInfoService;
import com.chen.gulimallproduct.vo.web.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * @author chen
 * @date 2022.06.11 11:13
 */
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    @ResponseBody
    @GetMapping("/{skuId}.item")
    public SkuItemVo skuItem(@PathVariable("skuId") Long skuId)
    {
        SkuItemVo vo = null;
        try {
            vo = skuInfoService.item(skuId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return vo;
    }
}
