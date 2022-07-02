package com.chen.ware.exception;

/**
 * @author chen
 * @date 2022.06.26 10:59
 */
public class LockFailException extends RuntimeException{

    public LockFailException(Long skuId)
    {
        super("商品号"+skuId+"锁库存失败");
    }
}
