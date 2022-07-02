package com.chen.authserver.exception;

/**
 * @author chen
 * @date 2022.06.16 13:23
 */
public class VerifyCodeException extends CommonMessageException{
    public VerifyCodeException(int code,String message)
    {
        super(code,message);
    }

}
