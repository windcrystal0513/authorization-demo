package com.thoughtworks.demo.common;

/**
 * 公共常量类
 *
 * @author zifangsky
 * @date 2018/7/25
 * @since 1.0.0
 */
public class Constants {
    /**
     * 用户信息在session中存储的变量名
     */
    public static final String SESSION_USER = "SESSION_USER";

    /**
     * 登录页面的回调地址在session中存储的变量名
     */
    public static final String SESSION_LOGIN_REDIRECT_URL = "LOGIN_REDIRECT_URL";

    /**
     * 授权页面的回调地址在session中存储的变量名
     */
    public static final String SESSION_AUTH_REDIRECT_URL = "SESSION_AUTH_REDIRECT_URL";


    public static final String LoginTIP ="登录成功";
    public static final String RegisterTIP="注册成功";
    public static final String VerifyTIP="验证成功";
}
