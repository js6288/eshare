package com.shu.eshare.filter;

import com.google.gson.Gson;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.service.impl.TokenService;
import com.shu.eshare.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private TokenService tokenService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        LoginUser loginUser;
        try {
            loginUser = tokenService.getLoginUser(request);
        } catch (BusinessException e) {//access_token过期处理
            BaseResponse baseResponse = ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
            response.setStatus(401);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(baseResponse));
            response.getWriter().flush();
            response.getWriter().close();
            log.info("access_token解析异常");
            return;
        }

        if (loginUser != null){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //存入SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }


        filterChain.doFilter(request,response);
    }
}
