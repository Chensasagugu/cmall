package com.chen.cmallproduct.service.impl;

import com.chen.cmallproduct.entity.AttrEntity;
import com.chen.cmallproduct.service.AttrAttrgroupRelationService;
import com.chen.cmallproduct.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

import com.chen.cmallproduct.dao.AttrGroupDao;
import com.chen.cmallproduct.entity.AttrGroupEntity;
import com.chen.cmallproduct.service.AttrGroupService;
import org.springframework.util.StringUtils;



@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Cacheable(value = "attrGroup",key = "#catelogId")
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        System.out.println("走数据库");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String)params.get("key");
        if(StringUtils.hasLength(key))
        {
            wrapper.and(new Consumer<QueryWrapper<AttrGroupEntity>>() {
                @Override
                public void accept(QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper) {
                    attrGroupEntityQueryWrapper.eq("attr_group_id",key).or().like("attr_group_name",key);
                }
            });
        }
        if(catelogId==0)
        {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
        wrapper = wrapper.eq("catelog_id",catelogId);
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据分类id查出所有的属性分组以及组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    @Cacheable(value = "attrGroup",key = "'detail'+#catelogId")
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //查询所有属性
        List<AttrGroupWithAttrsVo> vos = new ArrayList<>();
        for(AttrGroupEntity attrGroup:attrGroupEntities)
        {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroup,vo);
            List<AttrEntity> attrList = attrAttrgroupRelationService.getAttrList(attrGroup.getAttrGroupId());
            vo.setAttrs(attrList);
            vos.add(vo);
        }
        return vos;
    }

}