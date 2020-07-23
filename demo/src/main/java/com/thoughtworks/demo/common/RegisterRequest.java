package com.thoughtworks.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Pattern(regexp = "[a-zA-Z0-9_]{4,16}",message = "用户名格式不符合要求")
    private String userName;

    @Pattern(regexp = "^1(3|4|5|7|8)\\d{9}$",message = "手机号码格式不符合要求")
    private String phoneNumber;

    @Email(message = "邮箱格式不符合要求")
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9_]\\w{5,19}$",message = "密码格式不符合要求")
    private String password;
}
