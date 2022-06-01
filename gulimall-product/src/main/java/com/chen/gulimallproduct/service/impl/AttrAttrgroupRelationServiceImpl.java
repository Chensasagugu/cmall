package com.chen.gulimallproduct.service.impl;

import com.chen.common.constant.ProductConstant;
import com.chen.gulimallproduct.dao.AttrDao;
import com.chen.gulimallproduct.dao.AttrGroupDao;
import com.chen.gulimallproduct.entity.AttrEntity;
import com.chen.gulimallproduct.entity.AttrGroupEntity;
import com.chen.gulimallproduct.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallproduct.dao.AttrAttrgroupRelationDao;
import com.chen.gulimallproduct.entity.AttrAttrgroupRelationEntity;
import com.chen.gulimallproduct.service.AttrAttrgroupRelationService;
import org.springframework.util.StringUtils;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    AttrDao attrDao;
    @Autowired
    AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> getAttrList(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> list = this.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrgroupId)
        );
        List<AttrEntity> resList = new ArrayList<>();
        for(AttrAttrgroupRelationEntity relation:list)
        {
            Long attrId = relation.getAttrId();
            AttrEntity attr = attrDao.selectById(attrId);
            if(attr!=null)
            {
                resList.add(attr);
            }
        }
        return resList;
    }

    @Override
    public void deleteItems(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> list = new ArrayList<>();
        for(AttrGroupRelationVo vo:vos)
        {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo,entity);
            list.add(entity);
        }
        this.baseMapper.deleteBatchRelation(list);
    }

    @Override
    public PageUtils getNoAttrList(Map<String, Object> params, Long attrGroupId) {
        //获得所有已经被关联的属性id
        List<AttrAttrgroupRelationEntity> relationEntities = this.list();
        List<Long> attrIds = new ArrayList<>();
        for(AttrAttrgroupRelationEntity entity:relationEntities)
            attrIds.add(entity.getAttrId());
        //获得当前分组信息
        AttrGroupEntity attrGroup = attrGroupDao.selectById(attrGroupId);
        //SQL筛选条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id",attrGroup.getCatelogId())
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null&&attrIds.size()>0)
            wrapper.notIn("attr_id",attrIds);
        String key = (String) params.get("key");
        if(StringUtils.hasLength(key))
            wrapper.and(new Consumer<QueryWrapper<AttrEntity>>() {
                @Override
                public void accept(QueryWrapper<AttrEntity> attrEntityQueryWrapper) {
                    wrapper.eq("attr_id",key).or().like("attr_name",key);
                }
            });
        IPage<AttrEntity> page = attrDao.selectPage(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> relations = new ArrayList<>();
        for(AttrGroupRelationVo vo:vos)
        {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo,entity);
            relations.add(entity);
        }
        this.saveBatch(relations);
    }

}