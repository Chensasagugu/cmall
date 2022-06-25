package com.chen.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"验证码获取频率太高，稍后再试"),
    SMS_CODE_WRONG_EXCEPTION(10003,"验证码错误，请重新输入"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架失败"),
    USER_EXIST_EXCEPTION(15001,"用户存在异常"),
    USER_NOT_EXIST_EXCEPTION(15002,"用户不存在"),
    LOGIN_WRONG_PASSWORD(15003,"密码错误"),
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
