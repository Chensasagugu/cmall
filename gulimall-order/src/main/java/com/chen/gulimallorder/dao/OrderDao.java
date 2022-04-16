package com.chen.gulimallorder.dao;

import com.chen.gulimallorder.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:30:06
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
