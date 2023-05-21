package com.shu.eshare.model.request;

import lombok.Data;

@Data
public class RegisterRequestBody {
    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 手机验证码
     */
    private String phoneCode;

    /**
     * uuid
     */
    private String uuid;

}
