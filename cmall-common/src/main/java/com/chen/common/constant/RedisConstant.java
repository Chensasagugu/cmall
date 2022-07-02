package com.chen.common.constant;

/**
 * @author chen
 * @date 2022.05.29 14:27
 */
public class RedisConstant {
    public enum KeyEnum
    {
        CATEGORY_APP("app:categoryTree","前后端分离的三级分类树"),
        CATEGORY_WEB("web:categoryTree","web端的三级分类树"),
        CART_DATA("mall:cart","购物车缓存的前缀"),
        ORDER_TOKEN_PREFIX("mall:order:token","订单放重码前缀");
        String key;
        String describe;
        KeyEnum(String key,String describe)
        {
            this.key = key;
            this.describe = describe;
        }
        public String getKey()
        {
            return key;
        }
        public String getDescribe()
        {
            return describe;
        }
    }
}
