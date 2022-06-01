package com.chen.gulimallcoupon.service.impl;

import com.chen.common.to.MemberPrice;
import com.chen.common.to.SkuReductionTo;
import com.chen.gulimallcoupon.entity.MemberPriceEntity;
import com.chen.gulimallcoupon.entity.SkuLadderEntity;
import com.chen.gulimallcoupon.service.MemberPriceService;
import com.chen.gulimallcoupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallcoupon.dao.SkuFullReductionDao;
import com.chen.gulimallcoupon.entity.SkuFullReductionEntity;
import com.chen.gulimallcoupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo reductionTo) {
        //打折
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(reductionTo.getSkuId());
        skuLadderEntity.setFullCount(reductionTo.getFullCount());
        skuLadderEntity.setDiscount(reductionTo.getDiscount());
        skuLadderEntity.setAddOther(reductionTo.getCountStatus());
        if(reductionTo.getFullCount()>0)
            skuLadderService.save(skuLadderEntity);
        //满减
        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        skuFullReduction.setSkuId(reductionTo.getSkuId());
        BeanUtils.copyProperties(reductionTo,skuFullReduction);
        if(skuFullReduction.getFullPrice().compareTo(new BigDecimal("0"))==1)
            this.save(skuFullReduction);
        //会员价
        List<MemberPrice> memberPrices = reductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntityList = new ArrayList<>();
        for(MemberPrice memberPrice:memberPrices)
        {
            if(memberPrice.getPrice().compareTo(new BigDecimal("0"))==-1)
                continue;
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(reductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            memberPriceEntity.setAddOther(1);
            memberPriceEntityList.add(memberPriceEntity);
        }
        memberPriceService.saveBatch(memberPriceEntityList);
    }

}