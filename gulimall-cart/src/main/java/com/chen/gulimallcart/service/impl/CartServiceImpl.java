package com.chen.gulimallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chen.common.constant.RedisConstant;
import com.chen.common.utils.R;
import com.chen.gulimallcart.feign.ProductFeignService;
import com.chen.gulimallcart.interceptor.AuthorizationInterceptor;
import com.chen.gulimallcart.service.CartService;
import com.chen.gulimallcart.vo.CartVo;
import com.chen.gulimallcart.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chen
 * @date 2022.06.20 10:17
 */
@Service("CartService")
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public void add(Long skuId, int count) {
        BoundHashOperations<String, Long, CartVo.CartItem> ops = getBoundOps();

        CartVo.CartItem item = ops.get(skuId);
        if(item!=null)
        {
            //如果购物车里已经有了这个商品
            item.setCount(item.getCount()+count);
            item.setTotalPrice(item.getTotalPrice());
            ops.put(skuId,item);
        }else
        {
            //如果购物车里还没有这个商品
            CartVo.CartItem newItem = new CartVo.CartItem();
            newItem.setCheck(true);
            newItem.setSkuId(skuId);
            newItem.setCount(count);
            //从product服务中获得sku信息
            CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.info(skuId);
                if (r.getCode() == 0) {
                    //成功
                    SkuInfoVo skuInfoVo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    System.out.println(skuInfoVo.getSkuName());
                    newItem.setTitle(skuInfoVo.getSkuTitle());
                    newItem.setPrice(skuInfoVo.getPrice());
                    newItem.setImage(skuInfoVo.getSkuDefaultImg());
                    newItem.setTotalPrice(newItem.getTotalPrice());
                } else {
                    throw new RuntimeException(r.getMessage());
                }
            }, executor);
            //销售属性信息
            CompletableFuture<Void> attrValueTask = CompletableFuture.runAsync(() -> {
                R attrInfoReturn = productFeignService.simpleInfo(skuId);
                List<String> attrInfo = (List<String>) attrInfoReturn.get("data");
                newItem.setSkuAttr(attrInfo);
            }, executor);

            try {
                CompletableFuture.allOf(skuInfoTask,attrValueTask).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //将购物车项加入到redis缓存
            ops.put(skuId,newItem);
        }


    }

    @Override
    public void deleteItem(Long skuId, int count) {
        BoundHashOperations<String, Long, CartVo.CartItem> ops = getBoundOps();
        CartVo.CartItem item = ops.get(skuId);
        if(item!=null)
        {
            //购物车中有这个商品
            int remain = item.getCount()-count>0?item.getCount()-count:0;
            if(remain==0)
            {
                ops.delete(skuId);
            }else{
                item.setCount(remain);
                ops.put(skuId,item);
            }
        }else{
            //购物车中没有这个商品
            //TODO 应该抛出异常
        }
    }

    /***
     * 获得选中的购物项
     * @return
     */
    @Override
    public List<CartVo.CartItem> getCurrentUerCartItems() {
        List<CartVo.CartItem> cartItems = getCartItems();
        //查询最新的价格
        int i = 0;
        while(i<cartItems.size())
        {
            CartVo.CartItem item = cartItems.get(i);
            if(!item.getCheck())
                cartItems.remove(i);
            else{
                item.setPrice(productFeignService.getSkuPrice(item.getSkuId()));
                i++;
            }
        }
        return cartItems;
    }

    /***
     * 获得所有购物项
     * @return
     */
    @Override
    public List<CartVo.CartItem> getCartItems() {
        BoundHashOperations<String, Long, CartVo.CartItem> ops = getBoundOps();
        List<CartVo.CartItem> cartItems = ops.multiGet(ops.keys());
        return cartItems;
    }

    private BoundHashOperations<String, Long, CartVo.CartItem> getBoundOps()
    {
        Long userId = AuthorizationInterceptor.userIdThreadLocal.get();
        String cartKey = RedisConstant.KeyEnum.CART_DATA.getKey()+":"+userId.toString();
        BoundHashOperations<String, Long, CartVo.CartItem> ops = redisTemplate.boundHashOps(cartKey);
        return ops;
    }
}
