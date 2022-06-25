package com.chen.gulimallproduct.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.chen.common.valid.AddGroup;
import com.chen.common.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.chen.gulimallproduct.entity.BrandEntity;
import com.chen.gulimallproduct.service.BrandService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 14:24:04
 */
@RestController
@RequestMapping("gulimallproduct/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallproduct:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("gulimallproduct:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 品牌名
     */
    @GetMapping("/brandName/{brandId}")
    //@RequiresPermissions("gulimallproduct:brand:info")
    public String brandName(@PathVariable("brandId") Long brandId){
        BrandEntity brand = brandService.getById(brandId);

        return brand.getName();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallproduct:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){

		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallproduct:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateById(brand);
        if(StringUtils.hasLength(brand.getName()))
        {
            brandService.updateRelativeColomn(brand.getBrandId(),brand.getName());
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallproduct:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
