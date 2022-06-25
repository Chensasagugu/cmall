package com.chen.gulimallcart.service;

import com.chen.gulimallcart.vo.CartVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {

    /***
     * 添加购物车操作
     * @param skuId     sku商品的id
     * @param count     商品数量
     */
    void add(Long skuId,int count);

    void deleteItem(Long skuId, int count);

    List<CartVo.CartItem> getCurrentUerCartItems();

    List<CartVo.CartItem> getCartItems();
}
