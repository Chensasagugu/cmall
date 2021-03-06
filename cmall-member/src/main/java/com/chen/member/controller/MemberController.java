package com.chen.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.chen.common.to.UserLoginTo;
import com.chen.common.valid.login.LoginGroup;
import com.chen.common.valid.login.RegisterGroup;
import com.chen.member.feign.CouponFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.chen.member.entity.MemberEntity;
import com.chen.member.service.MemberService;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.R;



/**
 * 会员
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:25:40
 */
@RestController
@RequestMapping("gulimallmember/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;

    @GetMapping("/{memberId}/integration")
    public Integer getIntegration(@PathVariable("memberId") Long memberId)
    {
        MemberEntity byId = memberService.getById(memberId);
        return byId.getIntegration();
    }

    /*
    * 远程调用gulimall-coupon微服务
    * */
    @RequestMapping("/feigntest")
    public R test()
    {
        MemberEntity member = new MemberEntity();
        member.setNickname("chen");
        R r = couponFeignService.oneCoupon();
        return R.ok().put("member",member).put("coupon",r.get("coupon"));
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallmember:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimallmember:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallmember:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallmember:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallmember:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/register")
    public R register(@Validated(RegisterGroup.class) @RequestBody UserLoginTo userLoginTo)
    {
        memberService.register(userLoginTo);
        return R.ok("注册成功");
    }

    @RequestMapping("/login")
    public R login(@Validated(LoginGroup.class) @RequestBody UserLoginTo userLoginTo)
    {
        MemberEntity member = memberService.login(userLoginTo);
        return R.ok("登录成功").put("data",member.getId());
    }
}
