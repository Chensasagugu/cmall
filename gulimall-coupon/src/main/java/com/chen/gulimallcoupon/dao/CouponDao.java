package com.chen.gulimallcoupon.dao;

import com.chen.gulimallcoupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:16:14
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
