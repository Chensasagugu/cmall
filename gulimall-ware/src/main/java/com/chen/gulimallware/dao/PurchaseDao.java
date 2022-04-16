package com.chen.gulimallware.dao;

import com.chen.gulimallware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:41
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
