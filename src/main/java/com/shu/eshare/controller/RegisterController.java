package com.shu.eshare.controller;

import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.request.RegisterRequestBody;
import com.shu.eshare.model.response.UserResponse;
import com.shu.eshare.service.RegisterService;
import com.shu.eshare.utils.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/register")
public class RegisterController {


    @Resource
    public RegisterService registerService;


    /**
     * 传统注册
     * @param registerRequestBody 用户注册表单
     * @return
     */
    @PostMapping("/common")
    public BaseResponse<UserResponse> commonRegister(@RequestBody RegisterRequestBody registerRequestBody){
        //1.校验
        if (registerRequestBody == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(
                registerRequestBody.getUsername(),
                registerRequestBody.getNickname(),
                registerRequestBody.getPassword(),
                registerRequestBody.getPhone(),
                registerRequestBody.getUuid(),
                registerRequestBody.getPhoneCode()
        )){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //注册逻辑
        UserResponse userResponse = registerService.register(registerRequestBody);
        if (userResponse == null){
            ResultUtils.error(ErrorCode.PARAMS_ERROR,"用户注册失败");
        }
        return ResultUtils.success(userResponse);
    }
}
