package com.chen.authserver.exception;

/**
 * @author chen
 * @date 2022.06.16 15:38
 */
public class RegisterFailException extends CommonMessageException{
    public RegisterFailException(int code,String message)
    {
        super(code,message);
    }
}
