package com.chen.cmallproduct.controller;

import java.util.Arrays;
import java.util.Map;

import com.chen.cmallproduct.vo.spusave.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chen.cmallproduct.entity.SpuInfoEntity;
import com.chen.cmallproduct.service.SpuInfoService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * spu信息
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 14:24:04
 */
@RestController
@RequestMapping("gulimallproduct/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallproduct:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimallproduct:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 根据skuId获得spu信息
     */
    @GetMapping("/infoBySkuId/{skuId}")
    public R getInfoBySkuId(@PathVariable("skuId") Long skuId)
    {
        SpuInfoEntity spuInfo = spuInfoService.getBySkuId(skuId);
        return R.ok().setData(spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallproduct:spuinfo:save")
    public R save(@RequestBody SpuSaveVo saveVo){
		spuInfoService.saveSpuInfo(saveVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallproduct:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallproduct:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /*
    * 商品上架
    * */
    @RequestMapping("{spuId}/up")
    public R up(@PathVariable("spuId")Long spuId){

        spuInfoService.up(spuId);
        return R.ok();
    }

}
