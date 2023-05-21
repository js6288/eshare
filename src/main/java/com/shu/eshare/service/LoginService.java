package com.shu.eshare.service;

import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.response.Token;
import com.shu.eshare.model.response.UserResponse;

public interface LoginService {
    Token loginByPassword(String username, String password);

    UserResponse registerByPhone(String phone);

    UserResponse generateLoginUser(User registerUser, Integer level);

    UserResponse loginByPhone(User user);
}
