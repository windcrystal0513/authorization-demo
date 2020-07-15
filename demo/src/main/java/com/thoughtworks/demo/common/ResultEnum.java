package com.thoughtworks.demo.common;

/**
 * 枚举管理所有失败原因
 */
public enum ResultEnum {
    SUCCESS(200, "成功"),
    UNKONW_ERROR(-1, "未知错误"),
    NOT_DEVELOP_ERROR(9, "API尚未开发"),
    ARGUMENTS_DEVELOP_ERROR(10, "API参数错误，逻辑不合理"),

    //用户登录
    LOGIN_ERROR(176, "登录失败：用户不存在"),
    LOGIN_ERROR_1(254, "登录失败：密码错误"),

    //验证token
    VERIFY_ERROR(255, "token错误"),
    VERIFY_ERROR_1(256, "token过期"),


    //用户注册
    REGISTER_ERROR_1(251, "注册失败：用户名已注册过"),
    REGISTER_ERROR_2(252, "注册失败：电话已注册过"),
    REGISTER_ERROR_3(253, "注册失败：邮箱已注册过"),

    //oauth and sso
    INVALID_REQUEST(11,"请求缺少某个必需参数，包含一个不支持的参数或参数值，或者格式不正确。"),
    INVALID_CLIENT(12,"请求的client_id或client_secret参数无效。"),
    INVALID_GRANT(13,"请求的Authorization Code、Access Token、Refresh Token等信息是无效的。"),
    UNSUPPORTED_GRANT_TYPE(14,"不支持的grant_type。"),
    INVALID_SCOPE(15,"请求的scope参数是无效的、未知的、格式不正确的，或所请求的权限范围超过了数据拥有者所授予的权限范围。"),
    EXPIRED_TOKEN(16,"请求的Access Token或Refresh Token已过期。"),
    REDIRECT_URI_MISMATCH(17,"请求的redirect_uri所在的域名与开发者注册应用时所填写的域名不匹配。"),
    INVALID_REDIRECT_URI(18,"请求的回调URL不在白名单中。"),
    UNKNOWN_ERROR(19,"程序发生未知异常，请联系管理员解决。");

    ;

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
