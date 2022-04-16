package com.chen.gulimallcoupon.dao;

import com.chen.gulimallcoupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:16:14
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}
