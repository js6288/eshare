package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.mapper.UserMapper;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.request.RegisterRequestBody;
import com.shu.eshare.model.response.Token;
import com.shu.eshare.model.response.UserResponse;
import com.shu.eshare.model.vo.UserVO;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.service.LoginService;
import com.shu.eshare.service.RegisterService;
import com.shu.eshare.service.SmsService;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SmsService smsService;

    @Resource
    private LoginService loginService;

    @Resource
    private LevelService levelService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequestBody registerRequestBody) {
        //用户名长度为5-20，且不能重复
        int usernameLength = registerRequestBody.getUsername().length();
        if (usernameLength < 5 || usernameLength > 30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username",registerRequestBody.getUsername()));
        if (ObjectUtils.isNotEmpty(user)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名已经存在");
        }
        //昵称长度位1-30位
        int nickNameLength = registerRequestBody.getNickname().length();
        if (nickNameLength<1 || nickNameLength>30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //密码长度为8-16位
        int passwordLength = registerRequestBody.getPassword().length();
        if (passwordLength < 8 || passwordLength > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查询手机号验证码是否已经注册
        if(userMapper.selectOne(new QueryWrapper<User>().select("username").eq("phone",registerRequestBody.getPhone())) != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该手机号已注册");
        }
        //手机号验证码校验
        boolean isVerify = smsService.verifyMessage(registerRequestBody.getPhoneCode(), registerRequestBody.getUuid(), registerRequestBody.getPhone());
        if (!isVerify){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误或已过期");
        }

        User registerUser = new User();
        BeanUtils.copyProperties(registerRequestBody,registerUser);

        //加密
        registerUser.setPassword(SecurityUtils.encryptPassword(registerUser.getPassword()));

        //注册赠送200积分
        registerUser.setAccumulatePoints(200L);
        registerUser.setAvatarUrl(Constant.AVATAR_URL);
        registerUser.setPersonalSignature(Constant.PERSONAL_SIGNATURE);
        //插入数据
        int insert = userMapper.insert(registerUser);

        //插入成功
        if (SqlHelper.retBool(insert)){
            //创建等级
            Level level = new Level();
            level.setUserId(registerUser.getUserId());
            level.setLevel(1);
            levelService.save(level);

            //用户认证
            //生成access_token 和 refresh_token
            //执行登录流程，获取token
            Token token = loginService.loginByPassword(registerRequestBody.getUsername(), registerRequestBody.getPassword());

            //查询用户完整信息
            User user1 = userMapper.selectById(registerUser.getUserId());
            //用户信息脱敏
            UserVO securityUser = SecurityUtils.securityUserStrict(user1);

            securityUser.setLevel(level.getLevel());
            //生成返回体
            UserResponse userResponse = new UserResponse(securityUser, token);

            return userResponse;
        }
        return null;
    }
}
