package com.chen.gulimallorder.dao;

import com.chen.gulimallorder.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:30:05
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
