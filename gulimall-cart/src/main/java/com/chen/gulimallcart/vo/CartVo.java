package com.chen.gulimallcart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author chen
 * @date 2022.06.19 16:34
 */
public class CartVo{
    private List<CartItem> items;

    //商品数量
    private Integer countNum;

    //商品类型数量
    private Integer countType;

    //商品总价
    private BigDecimal totalAmount;

    private BigDecimal reduce = new BigDecimal("0");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int res = 0;
        if(items!=null)
            for(CartItem item:items)
                res+=item.getCount();
        return res;
    }


    public Integer getCountType() {
        return items.size();
    }


    public BigDecimal getTotalAmount() {
        BigDecimal price = new BigDecimal("0");
        //获得购物项总价
        if(items!=null)
            for(CartItem item:items)
                price = price.add(item.totalPrice);
        //减去优惠总价
        price = price.subtract(getReduce());
        return price;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }

    public static class CartItem
    {
        private Long skuId;
        private Boolean check = true;
        private String title;
        private List<String> skuAttr;
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice;
        private String image;

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public Boolean getCheck() {
            return check;
        }

        public void setCheck(Boolean check) {
            this.check = check;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getSkuAttr() {
            return skuAttr;
        }

        public void setSkuAttr(List<String> skuAttr) {
            this.skuAttr = skuAttr;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public BigDecimal getTotalPrice() {
            return this.price.multiply(new BigDecimal(""+this.count));
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
