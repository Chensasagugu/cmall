package com.chen.gulimallcoupon.dao;

import com.chen.gulimallcoupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:16:14
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
