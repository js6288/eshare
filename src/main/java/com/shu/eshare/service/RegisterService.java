package com.shu.eshare.service;

import com.shu.eshare.model.request.RegisterRequestBody;
import com.shu.eshare.model.response.UserResponse;

public interface RegisterService {

    UserResponse register(RegisterRequestBody registerRequestBody);
}
