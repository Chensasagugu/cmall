package com.chen.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.to.UserLoginTo;
import com.chen.common.utils.PageUtils;
import com.chen.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:25:40
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserLoginTo userLoginTo);

    MemberEntity login(UserLoginTo userLoginTo);
}

