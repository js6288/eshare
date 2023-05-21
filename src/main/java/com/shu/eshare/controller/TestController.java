package com.shu.eshare.controller;

import com.google.gson.Gson;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.annotation.RateLimit;
import com.shu.eshare.service.TestService;
import com.shu.eshare.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@Slf4j
public class TestController {

    @Resource
    private TestService testService;

    @RateLimit(key = "testGet",time = 60,count = 1,ipLimit = true)
    @GetMapping("/test")
    public String test(){
        System.out.println("api/test 测试");
        log.info("api/test 测试");
        return "test ok 测试";
    }

    @GetMapping("/test2")
    public String test2(){
        testService.testAsync();

        return "ok";
    }

    @GetMapping("/test3")
    public void test3(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseResponse baseResponse = ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        response.setStatus(401);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(baseResponse));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
