package com.chen.ware.service.impl;

import com.chen.ware.exception.LockFailException;
import com.chen.ware.mq.MyMQConfig;
import com.chen.ware.vo.LockStockVo;
import com.chen.ware.vo.SkuHasStockVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.ware.dao.WareSkuDao;
import com.chen.ware.entity.WareSkuEntity;
import com.chen.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> list = this.baseMapper.getSkuHasStock(skuIds);

        return list;
    }

    @Override
    @Transactional
    public boolean lockStock(List<LockStockVo> lockStockVos) {
        //找出所有有库存的仓库，锁住其中一个仓库
        for(LockStockVo lockInfo:lockStockVos)
        {
            List<Long> wareIds = this.baseMapper.getWareHashStock(lockInfo);
            if (wareIds!=null&&wareIds.size()>0)
            {
                //锁住其中一个仓库中的库存
                for(Long wareId:wareIds)
                {
                    Integer n = this.baseMapper.lockWareStock(wareId,lockInfo);
                    if(n<=0)
                        throw new LockFailException(lockInfo.getSkuId());
                }
            }else {
                throw new LockFailException(lockInfo.getSkuId());
            }
        }
        //将锁库存的消息发送给延时队列
        rabbitTemplate.convertAndSend(MyMQConfig.STOCK_EVENT_EXCHANGE,
                MyMQConfig.EXCHANGE_DELAY_ROUTING_KEY,
                lockStockVos);
        return true;
    }

}