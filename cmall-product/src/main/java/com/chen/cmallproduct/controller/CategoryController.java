package com.chen.cmallproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.chen.common.annotation.Login;
import com.chen.common.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chen.cmallproduct.entity.CategoryEntity;
import com.chen.cmallproduct.service.CategoryService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 商品三级分类
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 14:24:04
 */
@RestController
@RequestMapping("gulimallproduct/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /*
    * 三级分类列表
    * */
    @Login
    @RequestMapping("/list/tree")
    public R listTree()
    {
        List<CategoryEntity> list = categoryService.listTree();
        return R.ok().put("data",list);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallproduct:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("gulimallproduct:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallproduct:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallproduct:category:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody CategoryEntity category){
		categoryService.updateById(category);
        if(StringUtils.hasLength(category.getName()))
        {
            categoryService.updateRelativeColomn(category.getCatId(),category.getName());
        }
        return R.ok();
    }

    /**
     * 删除
     * @ResponseBody:获取请求体，必须发送post请求
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallproduct:category:delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
