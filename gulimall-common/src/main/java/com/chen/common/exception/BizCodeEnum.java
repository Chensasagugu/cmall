package com.chen.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"验证码获取频率太高，稍后再试"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架失败"),
    USER_EXIST_EXCEPTION(15001,"用户存在异常"),
    PHONE_EXIST_EXCEPTION(15002,"手机号存在异常");

    private int code;
    private String msg;

    BizCodeEnum(int code,String msg)
    {
        this.code = code;
        this.msg = msg;
    }
    public int getCode()
    {
        return code;
    }
    public String getMsg()
    {
        return msg;
    }
}
