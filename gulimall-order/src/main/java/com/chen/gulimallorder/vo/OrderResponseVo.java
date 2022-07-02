package com.chen.gulimallorder.vo;

import com.chen.gulimallorder.entity.OrderEntity;
import lombok.Data;

/**
 * @author chen
 * @date 2022.06.24 13:36
 */
@Data
public class OrderResponseVo {

    private OrderEntity order;
    private Integer code;

    public enum ResponseCode
    {
        SUCCESS(0,"成功"),
        TOKEN_VALIDATION_FAIL(1,"订单TOKEN验证失败"),
        PRICE_VALIDATION_FAIL(2,"验价失败"),
        LOCK_STOCK_FAIL(3,"锁库存失败，没有库存了");
        int code;
        String msg;
        ResponseCode(int code,String msg)
        {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
