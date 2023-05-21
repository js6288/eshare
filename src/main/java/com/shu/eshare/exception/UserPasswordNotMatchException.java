package com.shu.eshare.exception;

import com.shu.eshare.common.ErrorCode;

public class UserPasswordNotMatchException extends BusinessException{

    public UserPasswordNotMatchException() {
        super("用户名或密码不正确", 401, "用户名或密码不正确");
    }
}
