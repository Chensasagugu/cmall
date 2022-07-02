package com.chen.gulimallware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.chen.common.utils.R;
import com.chen.gulimallware.feign.MemberFeignService;
import com.chen.gulimallware.vo.FareVo;
import com.chen.gulimallware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallware.dao.WareInfoDao;
import com.chen.gulimallware.entity.WareInfoEntity;
import com.chen.gulimallware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo addressVo = r.getData(new TypeReference<MemberAddressVo>() {
        });
        if(addressVo!=null)
        {
            String phone = addressVo.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            fareVo.setFare(new BigDecimal(substring));
            fareVo.setMemberAddressVo(addressVo);
            return fareVo;
        }
        return fareVo;
    }

}