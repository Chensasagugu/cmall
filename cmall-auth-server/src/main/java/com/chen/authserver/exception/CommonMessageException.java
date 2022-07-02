package com.chen.authserver.exception;

import lombok.Data;

/**
 * @author chen
 * @date 2022.06.17 14:47
 */
@Data
public class CommonMessageException extends RuntimeException{
    private int code;
    private String msg;

    public CommonMessageException(int code,String message)
    {
        this.code = code;
        this.msg = message;
    }
}
