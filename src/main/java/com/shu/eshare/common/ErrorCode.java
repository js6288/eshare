package com.shu.eshare.common;

/**
 * 错误码
 *
 * @author yupi
 */
public enum ErrorCode {

    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", "请求失败请稍后重试"),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    FORBIDDEN(40301,"禁止操作",""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    SMS_ERROR(50001,"短信发送失败",""),
    RATE_LIMIT_ERROR(40401,"接口访问过于频繁",""),
    OSS_POLICY_ERROR(50002,"获取服务端签名失败",""),
    JWT_EXPIRED_ERROR(401501,"access_token过期请刷新","access_token过期请刷新"),
    REFRESH_TOKEN_EXPIRED(401502,"用户过期请重新登录","用户过期请重新登录"),
    USER_NOT_EXIST(40503,"用户不存在" , "用户不存在");

    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
