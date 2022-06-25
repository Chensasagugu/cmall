package com.chen.gulimallauthserver.exception;

/**
 * @author chen
 * @date 2022.06.17 14:10
 */
public class LoginFailException extends CommonMessageException{

    public LoginFailException(int code,String message)
    {
        super(code,message);
    }
}
