package com.chen.common.to;

import com.chen.common.valid.login.LoginGroup;
import com.chen.common.valid.login.RegisterGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author chen
 * @date 2022.06.16 14:13
 */
@Data
public class UserLoginTo {

    @NotNull(groups = {RegisterGroup.class, LoginGroup.class},message = "手机号不能为空")
    //@Pattern(regexp = "/^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$/",message = "不是合法手机号",
    //groups = {RegisterGroup.class, LoginGroup.class})
    private String phone;

    @NotNull(groups = {RegisterGroup.class, LoginGroup.class})
    @Length(min = 6,max = 20,groups = {RegisterGroup.class, LoginGroup.class},message = "密码不合法")
    private String password;

    @NotNull(groups = {RegisterGroup.class})
    @Length(min = 6,max = 6,groups = {RegisterGroup.class, LoginGroup.class},message = "验证码输入错误")
    private String code;
}
