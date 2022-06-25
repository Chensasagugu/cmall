package com.chen.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallware.entity.WareInfoEntity;
import com.chen.gulimallware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:40
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /***
     * 根据收获地址计算运费
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

