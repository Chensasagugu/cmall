package com.chen.cmallproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.chen.cmallproduct.entity.AttrEntity;
import com.chen.cmallproduct.service.AttrAttrgroupRelationService;
import com.chen.cmallproduct.service.CategoryService;
import com.chen.cmallproduct.vo.AttrGroupRelationVo;
import com.chen.cmallproduct.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.chen.cmallproduct.entity.AttrGroupEntity;
import com.chen.cmallproduct.service.AttrGroupService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 属性分组
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 14:24:04
 */
@RestController
@RequestMapping("gulimallproduct/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    /**
     *获得分组关联的所有属性
     */
    @GetMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId)
    {
        List<AttrEntity> list = attrAttrgroupRelationService.getAttrList(attrGroupId);
        return R.ok().put("data",list);
    }

    /**
     * 获得某个分类下的所有属性组及属性
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId")Long catelogId)
    {
        //查出当前分类下所有属性分组
        //查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> list = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",list);
    }
    /**
     *获得没有与分组关联的所有属性
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrGroupId") Long attrGroupId)
    {
        PageUtils page = attrAttrgroupRelationService.getNoAttrList(params,attrGroupId);
        return R.ok().put("page",page);
    }
    /**
     * 属性组列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("gulimallproduct:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("gulimallproduct:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrGroup.setCateLogPath(catelogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @Caching(evict = {
            @CacheEvict(value = "attrGroup",key = "#p0.catelogId"),
            @CacheEvict(value = "attrGroup",key = "0")
    })
    @Transactional()
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallproduct:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }
    /**
     * 新增分组下关联属性
     */
    @PostMapping("/attr/relation")
    public R addAttr(@RequestBody List<AttrGroupRelationVo> vos)
    {
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallproduct:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @CacheEvict(value ="attrGroup",allEntries = true)
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallproduct:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }
    /**
     * 删除该分组下某一个属性
     */
    @PostMapping("/attr/relation/delete")
    public R deleteAttr(@RequestBody AttrGroupRelationVo[] vos){
        attrAttrgroupRelationService.deleteItems(vos);
        return R.ok();
    }
}
