package com.chen.gulimallproduct.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.chen.gulimallproduct.entity.BrandEntity;
import com.chen.gulimallproduct.vo.BrandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chen.gulimallproduct.entity.CategoryBrandRelationEntity;
import com.chen.gulimallproduct.service.CategoryBrandRelationService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 14:24:04
 */
@RestController
@RequestMapping("gulimallproduct/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @GetMapping("/catelog/list")
    public R list(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.queryByBrandId(brandId);
        return R.ok().put("data",list);
    }
    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId")Long catId)
    {
        List<BrandEntity> brands = categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVo> vos = new ArrayList<>();
        for(BrandEntity brand:brands)
        {
            BrandVo vo = new BrandVo();
            vo.setBrandId(brand.getBrandId());
            vo.setBrandName(brand.getName());
            vos.add(vo);
        }
        return R.ok().put("data",vos);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallproduct:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimallproduct:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallproduct:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallproduct:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallproduct:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
