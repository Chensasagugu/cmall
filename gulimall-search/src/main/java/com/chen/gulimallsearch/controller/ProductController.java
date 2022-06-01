package com.chen.gulimallsearch.controller;

import com.chen.common.constant.ESIndexEnum;
import com.chen.common.exception.BizCodeEnum;
import com.chen.common.to.SkuEsModel;
import com.chen.common.utils.R;
import com.chen.gulimallsearch.service.ProductEsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author chen
 * @date 2022.05.25 14:07
 */
@RestController
@RequestMapping("/esproduct")
public class ProductController {
    @Autowired
    ProductEsService productEsService;

    /*
    * 批量保存到ES中
    * */
    @RequestMapping("/batchsave")
    public R batchsave(@RequestBody List<SkuEsModel> esModels)
    {
        boolean b = false;
        try {
            b = productEsService.batchIndexProduct(esModels, ESIndexEnum.PRODUCT_INDEX.getIndexName());
        } catch (IOException e) {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (b)
            return R.ok();
        else
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }
}
