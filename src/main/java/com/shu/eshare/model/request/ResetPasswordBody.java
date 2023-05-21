package com.shu.eshare.model.request;

import lombok.Data;

@Data
public class ResetPasswordBody {

    /**
     * 新的密码
     */
    private String newPassword;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 验证码
     */
    private String code;
    /**
     * uuid
     */
    private String uuid;
}
