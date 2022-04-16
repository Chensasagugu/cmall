package com.chen.gulimallmember;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.gulimallmember.entity.MemberEntity;
import com.chen.gulimallmember.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class GulimallMemberApplicationTests {

    @Autowired
    MemberService memberService;
    @Test
    void contextLoads() {
        /*
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("chen");
        memberService.save(memberEntity);

         */
        MemberEntity memberEntity = new MemberEntity();
        List<MemberEntity> list = memberService.list(new QueryWrapper<MemberEntity>().eq("id",1L)
                .eq("birth",new Date("1998/7/9")));
        for(MemberEntity m:list)
            System.out.println(m);
    }

}
