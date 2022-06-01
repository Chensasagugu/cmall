package com.chen.gulimallproduct.service.impl;

import com.chen.common.constant.ProductConstant;
import com.chen.gulimallproduct.dao.AttrAttrgroupRelationDao;
import com.chen.gulimallproduct.dao.AttrGroupDao;
import com.chen.gulimallproduct.dao.CategoryDao;
import com.chen.gulimallproduct.entity.AttrAttrgroupRelationEntity;
import com.chen.gulimallproduct.entity.CategoryEntity;
import com.chen.gulimallproduct.vo.AttrRespVo;
import com.chen.gulimallproduct.vo.AttrVo;
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

import com.chen.gulimallproduct.dao.AttrDao;
import com.chen.gulimallproduct.entity.AttrEntity;
import com.chen.gulimallproduct.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AttrGroupDao attrGroupDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        //保存当前属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);
        //在属性-属性组关联表中保存关联关系
        if(attr.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()&&attr.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type",
                type.equalsIgnoreCase("base")?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        String key = (String) params.get("key");
        if(StringUtils.hasLength(key))
        {
            wrapper.or(new Consumer<QueryWrapper<AttrEntity>>() {
                @Override
                public void accept(QueryWrapper<AttrEntity> attrEntityQueryWrapper) {
                    attrEntityQueryWrapper.eq("attr_id",key).like("attr_name",key)
                            .like("value_select",key);
                }
            });
        }
        if(catelogId!=0)
        {
            wrapper.eq("catelog_id",catelogId);
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = new ArrayList<>();
        for(AttrEntity attr:records)
        {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attr,attrRespVo);
            //设置分类名
            CategoryEntity category = categoryDao.selectById(attr.getCatelogId());
            if(category!=null)
            {
                attrRespVo.setCatelogName(categoryDao.selectById(attr.getCatelogId()).getName());
            }
            //设置属性组名
            if("base".equalsIgnoreCase(type)){
                Long attrId = attr.getAttrId();
                if(attrId!=null) {
                    AttrAttrgroupRelationEntity attrAttrgroupRelation = attrAttrgroupRelationDao.selectOne(
                            new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId)
                    );
                    if(attrAttrgroupRelation!=null){
                        Long attrGroupId = attrAttrgroupRelation.getAttrGroupId();
                        if(attrGroupId!=null)
                            attrRespVo.setGroupName(attrGroupDao.selectById(attrGroupId).getAttrGroupName());
                    }
                }
            }
            respVos.add(attrRespVo);
        }
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attr = this.getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attr,attrRespVo);
        if(attr.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //获得属性组id
            AttrAttrgroupRelationEntity attrAttrgroupRelation = attrAttrgroupRelationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrId)
            );
            if(attrAttrgroupRelation!=null)
            {
                Long attrGroupId = attrAttrgroupRelation.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
            }
        }
        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);
        if(attr.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())
        {
            Long attrGroupId = attr.getAttrGroupId();
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            int count = attrAttrgroupRelationDao.selectCount(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId())
            );
            if(count>0)
            {
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity
                        ,new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else{
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

}