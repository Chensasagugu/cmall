package com.chen.gulimallproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.chen.gulimallproduct.entity.ProductAttrValueEntity;
import com.chen.gulimallproduct.service.CategoryService;
import com.chen.gulimallproduct.service.ProductAttrValueService;
import com.chen.gulimallproduct.vo.AttrRespVo;
import com.chen.gulimallproduct.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chen.gulimallproduct.entity.AttrEntity;
import com.chen.gulimallproduct.service.AttrService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 商品属性
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 14:24:04
 */
@RestController
@RequestMapping("gulimallproduct/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    /**
     * 查出商品的规格属性
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId)
    {
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",entities);
    }
    /**
     * 获得属性列表
     */
    @RequestMapping("/{type}/list/{catelogId}")
    //@RequiresPermissions("gulimallproduct:attr:list")
    public R baseAttrlist(@RequestParam Map<String, Object> params
            ,@PathVariable("catelogId") Long catelogId,@PathVariable("type") String type){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallproduct:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("gulimallproduct:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        //设置分类路径
        Long[] cateLogPath = categoryService.findCatelogPath(attrRespVo.getCatelogId());
        attrRespVo.setCatelogPath(cateLogPath);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallproduct:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("gulimallproduct:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallproduct:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallproduct:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
