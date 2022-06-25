package com.chen.gulimallmember.service.impl;

import com.chen.common.exception.BizCodeEnum;
import com.chen.common.to.UserLoginTo;
import com.chen.gulimallmember.exception.MemberLoginException;
import com.chen.gulimallmember.service.MemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallmember.dao.MemberDao;
import com.chen.gulimallmember.entity.MemberEntity;
import com.chen.gulimallmember.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserLoginTo userLoginTo) {
        //检查手机号是否已经被注册
        checkPhoneUnique(userLoginTo.getPhone());

        MemberEntity entity  = new MemberEntity();
        entity.setMobile(userLoginTo.getPhone());

        //设置密码加盐加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(userLoginTo.getPassword());
        entity.setPassword(encodePassword);

        //设置默认会员等级
        entity.setLevelId(memberLevelService.defaultLevel().getId());

        //设置默认用户名
        String nickname = "用户"+ UUID.randomUUID().toString().substring(0,6).toLowerCase();
        entity.setNickname(nickname);

        this.save(entity);
    }

    @Override
    public MemberEntity login(UserLoginTo userLoginTo) {
        MemberEntity member = this.getOne(new QueryWrapper<MemberEntity>().eq("mobile", userLoginTo.getPhone()));
        //判断用户是否存在
        if(member==null)
            throw new MemberLoginException(BizCodeEnum.USER_NOT_EXIST_EXCEPTION.getCode(),
                    BizCodeEnum.USER_NOT_EXIST_EXCEPTION.getMsg());
        //判断密码是否正确
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(userLoginTo.getPassword(),member.getPassword());
        //如果错误则抛出异常
        if(!matches)
            throw new MemberLoginException(BizCodeEnum.LOGIN_WRONG_PASSWORD.getCode(),
                    BizCodeEnum.LOGIN_WRONG_PASSWORD.getMsg());
        return member;
    }

    private void checkPhoneUnique(String phone)
    {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count>=1)
        {
            throw new MemberLoginException(BizCodeEnum.USER_EXIST_EXCEPTION.getCode()
                    ,BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
    }
}