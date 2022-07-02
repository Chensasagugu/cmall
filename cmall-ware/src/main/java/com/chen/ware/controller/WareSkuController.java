package com.chen.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.chen.ware.exception.LockFailException;
import com.chen.ware.vo.LockStockVo;
import com.chen.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chen.ware.entity.WareSkuEntity;
import com.chen.ware.service.WareSkuService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 商品库存
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:40
 */
@RestController
@RequestMapping("gulimallware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /***
     * 查看是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds)
    {
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().setData(vos);
    }

    /**
     * 锁库存
     * @param lockStockVos
     * @return
     */
    @PostMapping("/lockStock")
    public R lockSkuStock(@RequestBody List<LockStockVo> lockStockVos)
    {
        boolean isLocked;
        try{
            isLocked = wareSkuService.lockStock(lockStockVos);
        }catch (LockFailException e)
        {
            //锁失败
            return R.error();
        }
        if(isLocked)
        {
            return R.ok();
        }else
        {
            return R.error();
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimallware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
