package com.chen.cmallproduct.exception;

import com.chen.common.exception.BizCodeEnum;
import com.chen.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chen
 * @date 2022.04.25 10:40
 * 校验异常处理器
 */
@RestControllerAdvice(basePackages = "com.chen.gulimallproduct.controller")
public class ValidExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        Map<String,String> map = new HashMap<>();
        BindingResult result = e.getBindingResult();
        for(FieldError error:result.getFieldErrors())
            map.put(error.getField(), error.getDefaultMessage());
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg())
                .put("data",map);
    }
}
