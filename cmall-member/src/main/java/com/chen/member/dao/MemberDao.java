package com.chen.member.dao;

import com.chen.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:25:40
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
