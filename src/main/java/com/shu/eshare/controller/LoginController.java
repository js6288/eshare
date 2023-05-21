package com.shu.eshare.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.response.Token;
import com.shu.eshare.model.response.UserResponse;
import com.shu.eshare.model.vo.UserVO;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.service.LoginService;
import com.shu.eshare.service.SmsService;
import com.shu.eshare.service.UserService;
import com.shu.eshare.service.impl.TokenService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Resource
    public TokenService tokenService;

    @Resource
    public LoginService loginService;

    @Resource
    public UserService userService;

    @Resource
    public SmsService smsService;

    @Resource
    public LevelService levelService;


    /**
     * token刷新接口
     * @param request
     * @return
     */
    @PostMapping("/refresh/token")
    public BaseResponse<Token> refreshToken(HttpServletRequest request){
        //如果refresh_token过期，则返回失败，必须重新登录
        Token token = tokenService.refreshToken(request);
        if (ObjectUtils.isNull(token)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(token);
    }

    /**
     * 账号密码登录
     * @return
     */
    @PostMapping("/password")
    public BaseResponse<UserResponse> loginByPassword(String username, String password){
        if (StringUtils.isAnyBlank(username,password)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        Token token = loginService.loginByPassword(username,password);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        //脱敏
        UserVO userVO = SecurityUtils.securityUserStrict(user);
        //查询用户等级
        Level level = levelService.getOne(new QueryWrapper<Level>().select("level").eq("user_id", user.getUserId()));
        userVO.setLevel(level.getLevel());

        UserResponse userResponse = new UserResponse(userVO, token);
        return ResultUtils.success(userResponse);
    }

    /**
     * 手机号登录 / 注册
     */
    @PostMapping("phone")
    public BaseResponse<UserResponse> loginByPhone(String phone,String uuid,String code){
        //校验手机号
        if(!smsService.verifyMessage(code, uuid, phone)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误或已过期");
        }
        //查询手机号是否已经注册
        User user = userService.getOne(new QueryWrapper<User>().eq("phone", phone));

        if (user == null){//如果用户不存在，说明手机号没有注册，执行注册逻辑
            UserResponse userResponse = loginService.registerByPhone(phone);

            return ResultUtils.success(userResponse);
        }
        //如果手机号已经注册则直接登录
        UserResponse userResponse = loginService.loginByPhone(user);
        return ResultUtils.success(userResponse);
    }
}
