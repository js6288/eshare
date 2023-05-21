package com.shu.eshare.handler;

import com.google.gson.Gson;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理类 返回未授权
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {


        BaseResponse baseResponse = ResultUtils.error(ErrorCode.NOT_LOGIN, "用户认证失败请查询登录");

        String resultJson = new Gson().toJson(baseResponse);
        WebUtils.renderString(response,resultJson);
    }
}
