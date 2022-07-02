package com.chen.cart.controller;

import com.chen.common.annotation.Login;
import com.chen.common.utils.R;
import com.chen.cart.service.CartService;
import com.chen.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chen
 * @date 2022.06.19 17:18
 */
@RestController
public class CartController {

    @Autowired
    CartService cartService;


    @Login
    @PostMapping("/addToCart")
    public R addToCart(@RequestParam("skuId")Long skuId,@RequestParam("count") int count)
    {
        cartService.add(skuId,count);
        return R.ok();
    }

    @Login
    @PostMapping("/deleteCartItem")
    public R deleteCartItem(@RequestParam("skuId")Long skuId,@RequestParam("count") int count)
    {
        cartService.deleteItem(skuId,count);
        return R.ok();
    }

    @Login
    @GetMapping("getCartItems")
    public R getCartItems()
    {
        List<CartVo.CartItem> items = cartService.getCartItems();
        return R.ok().put("data",items);
    }

    @Login
    @GetMapping("/currentUserCartItems")
    public List<CartVo.CartItem> getCurrentUerCartItems()
    {
        return cartService.getCurrentUerCartItems();
    }
}
