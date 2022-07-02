package com.chen.member.exception;

import com.chen.common.utils.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author chen
 * @date 2022.06.16 15:24
 */
@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(MemberLoginException.class)
    public R loginExceptionHanler(MemberLoginException e)
    {
        return R.error(e.getCode(),e.getMessage());
    }
}
