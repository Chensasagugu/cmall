package com.chen.cmallproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chen.common.constant.RedisConstant;
import com.chen.cmallproduct.dao.CategoryBrandRelationDao;
import com.chen.cmallproduct.vo.web.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.cmallproduct.dao.CategoryDao;
import com.chen.cmallproduct.entity.CategoryEntity;
import com.chen.cmallproduct.service.CategoryService;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;
    @Autowired
    RedissonClient redissonClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 空结果缓存：解决缓存穿透
     * 设置过期时间（加随机值）：解决缓存雪崩
     * 加锁：解决缓存击穿,用redis实现分布式锁
     * @return
     */
    @Override
    public List<CategoryEntity> listTree() {
        //从redis缓存中查找
        String catelogJSON = stringRedisTemplate.opsForValue().get("catelogJSON");

        if(!StringUtils.hasLength(catelogJSON))
        {
            //从数据库中查找
            List<CategoryEntity> categoryList = listTreeByDBWithRedissonLock();
            return categoryList;
        }
        List<CategoryEntity> list = JSON.parseObject(
                catelogJSON,
                new TypeReference<List<CategoryEntity>>(){}
        );
        return list;
    }
    public List<CategoryEntity> listTreeByDBWithRedissonLock()
    {
        //获得锁,锁的名字。锁的粒度，越细越快
        RLock lock = redissonClient.getLock("CatelogJson-lock");
        lock.lock();
        //获得分布式锁成功
        List<CategoryEntity> list;
        try{
            String s = stringRedisTemplate.opsForValue().get(RedisConstant.KeyEnum.CATEGORY_APP.getKey());
            if(StringUtils.hasLength(s))
            {
                list = JSON.parseObject(
                        s,
                        new TypeReference<List<CategoryEntity>>(){}
                );
            }else{
                System.out.println("从数据库中查找");
                list = listTreeByDB();
            }
        }finally {
            //解锁
            lock.unlock();
        }
        return list;
    }
    public List<CategoryEntity> listTreeByDB() {

        List<CategoryEntity> list = baseMapper.selectList(null);
        List<CategoryEntity> roots = new ArrayList<>();
        for(CategoryEntity entity:list)
            if(entity.getParentCid()==0L)
                roots.add(entity);
        for(CategoryEntity root:roots)
            root.setChildren(getChildren(root,list));
        Collections.sort(roots, new Comparator<CategoryEntity>() {
            @Override
            public int compare(CategoryEntity o1, CategoryEntity o2) {
                return (o1.getSort()==null?0:o1.getSort())-(o2.getSort()==null?0:o2.getSort());
            }
        });
        //转化为json字符串并保存进缓存
        String s = JSON.toJSONString(roots);
        stringRedisTemplate.opsForValue().set(RedisConstant.KeyEnum.CATEGORY_APP.getKey(),s,1, TimeUnit.DAYS);
        return roots;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单，是否被别的地方引用

        //TODO 删除缓存中的三级分类树
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        Deque<Long> path = new LinkedList<>();
        CategoryEntity entity = this.getById(catelogId);
        path.offerFirst(catelogId);
        getParent(path,entity);
        return path.toArray(new Long[path.size()]);
    }

    @Override
    public void updateRelativeColomn(Long catId, String name) {
        //更新pms_category_brand_relation表中的分类名
        categoryBrandRelationDao.updateCatelogNameByCatelogId(catId,name);

        //TODO 更新其他表中的分类名
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> list = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return list;
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJSON() {
        String json = stringRedisTemplate.opsForValue().get(RedisConstant.KeyEnum.CATEGORY_WEB.getKey());
        if(!StringUtils.hasLength(json))
        {
            //从数据库中去查
            Map<String, List<Catalog2Vo>> resultMap = getCatelogJSONByDBWithRedissonLock();
            return resultMap;
        }
        Map<String, List<Catalog2Vo>> resultMap = JSON.parseObject(
                json,
                new TypeReference<Map<String,List<Catalog2Vo>>>(){}
                );
        return resultMap;
    }
    private Map<String, List<Catalog2Vo>> getCatelogJSONByDBWithRedissonLock(){
        RLock lock = redissonClient.getLock("web.catalogLock");
        lock.lock();
        try{
            String s = stringRedisTemplate.opsForValue().get(RedisConstant.KeyEnum.CATEGORY_WEB.getKey());
            if(!StringUtils.hasLength(s))
            {
                Map<String, List<Catalog2Vo>> map = getCatelogJSONByDB();
                String mapJson = JSON.toJSONString(map);
                stringRedisTemplate.opsForValue().set(RedisConstant.KeyEnum.CATEGORY_WEB.getKey(),mapJson,1,TimeUnit.DAYS);
                return map;
            }else{
                Map<String, List<Catalog2Vo>> resultMap = JSON.parseObject(
                        s,
                        new TypeReference<Map<String,List<Catalog2Vo>>>(){}
                );
                return resultMap;
            }
        }finally {
            lock.unlock();
        }
    }
    private Map<String, List<Catalog2Vo>> getCatelogJSONByDB(){
        //查出所有分类
        List<CategoryEntity> level1Categories = getLevel1Categories();

        Map<String,List<Catalog2Vo>> map = new HashMap<>();
        for(CategoryEntity level1Category:level1Categories)
        {
            List<Catalog2Vo> catalog2Vos = new ArrayList<>();
            //1级分类下的2级分类列表
            List<CategoryEntity> level2Categories = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", level1Category.getCatId()));
            for(CategoryEntity level2Category:level2Categories)
            {
                Catalog2Vo catalog2Vo = new Catalog2Vo();
                //2级分类下的3级分类列表
                List<CategoryEntity> level3Categories = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",level2Category.getCatId()));
                List<Catalog2Vo.Catalog3Vo> catelog3Vos = new ArrayList<>();
                for (CategoryEntity level3Category:level3Categories)
                {
                    Catalog2Vo.Catalog3Vo catelog3Vo = new Catalog2Vo.Catalog3Vo();
                    catelog3Vo.setCatalog2Id(level2Category.getCatId().toString());
                    catelog3Vo.setId(level3Category.getCatId().toString());
                    catelog3Vo.setName(level3Category.getName());
                    catelog3Vos.add(catelog3Vo);
                }
                catalog2Vo.setCatalog1Id(level1Category.getCatId().toString());
                catalog2Vo.setCatalog3List(catelog3Vos);
                catalog2Vo.setId(level2Category.getCatId().toString());
                catalog2Vo.setName(level2Category.getName());
                catalog2Vos.add(catalog2Vo);
            }
            map.put(level1Category.getCatId().toString(), catalog2Vos);
        }
        return map;
    }
    private void getParent(Deque<Long> path,CategoryEntity entity){
        if(entity.getParentCid()!=0)
        {
            path.offerFirst(entity.getParentCid());
            CategoryEntity parent = this.getById(entity.getParentCid());
            getParent(path,parent);
        }
    }

    private List<CategoryEntity> getChildren(CategoryEntity parent,List<CategoryEntity> list)
    {
        List<CategoryEntity> children = new ArrayList<>();
        for(CategoryEntity entity:list)
            if(entity.getParentCid()==parent.getCatId())
                children.add(entity);
        for(CategoryEntity entity:children)
            entity.setChildren(getChildren(entity,list));
        Collections.sort(children, new Comparator<CategoryEntity>() {
            @Override
            public int compare(CategoryEntity o1, CategoryEntity o2) {
                return (o1.getSort()==null?0:o1.getSort())-(o2.getSort()==null?0:o2.getSort());
            }
        });
        return children;
    }

}