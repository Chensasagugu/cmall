package com.chen.member.exception;

import lombok.Data;

/**
 * @author chen
 * @date 2022.06.16 15:15
 */
@Data
public class MemberLoginException extends RuntimeException {
    private int code;
    private String message;

    public MemberLoginException(int code,String message)
    {
        this.code = code;
        this.message = message;
    }
}
