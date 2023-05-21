package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.exception.UserPasswordNotMatchException;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.response.Token;
import com.shu.eshare.model.response.UserResponse;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.model.vo.UserVO;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.service.LoginService;
import com.shu.eshare.service.UserService;
import com.shu.eshare.utils.IdWorker;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenService tokenService;

    @Resource
    private UserService userService;

    @Resource
    private LevelService levelService;


    @Override
    public Token loginByPassword(String username, String password) {

        //AuthenticationManager authenticate进行用户认证
        Authentication authentication = null;

        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        }catch (Exception e){
            if (e instanceof BadCredentialsException){
                throw new UserPasswordNotMatchException();
            }else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户名不存在或密码错误");
            }
        }

        //如果认证通过了
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        //生成token
        String username1 = loginUser.getUsername();
        Token token = tokenService.createToken(username1);

        return token;
    }

    @Override
    @Transactional
    public UserResponse registerByPhone(String phone) {
        //生成默认用户信息
        User registerUser = new User();
        IdWorker idWorker = new IdWorker(1, 1, 1);
        long username_suffix = idWorker.nextId();
        registerUser.setUsername(Constant.USERNAME_PREFIX+username_suffix);
        registerUser.setNickname(Constant.NICKNAME_PREFIX+username_suffix);
        registerUser.setAccumulatePoints(200L);
        registerUser.setAvatarUrl(Constant.AVATAR_URL);
        registerUser.setPersonalSignature(Constant.PERSONAL_SIGNATURE);
        registerUser.setPhone(phone);
        //随机生成密码
        registerUser.setPassword(new BCryptPasswordEncoder().encode("123456"));
        //TODO 获取ip地址

        //保存用户信息
        boolean save = userService.save(registerUser);

        //保存用户等级信息
        Level level = new Level();
        level.setUserId(registerUser.getUserId());
        level.setLevel(1);
        levelService.save(level);

        if (!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户注册失败");
        }

        //TODO 手机号登录 目前采用一种取巧的方式
//        LoginUser loginUser = new LoginUser(SecurityUtils.securityUser(registerUser),new ArrayList<>());
//
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
//
//        //存入SecurityContextHolder
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//        //生成access_token和refresh_token
//        Token token = tokenService.createToken(registerUser.getUsername());
//        User byId = userService.getById(registerUser.getUserId());
//
//        UserVO userVO = SecurityUtils.securityUserStrict(byId);
//        userVO.setLevel(level.getLevel());
//
//        UserResponse userResponse = new UserResponse(userVO, token);
//
//        return userResponse;
        return generateLoginUser(registerUser,level.getLevel());
    }

    @Override
    public UserResponse generateLoginUser(User registerUser,Integer level){
        LoginUser loginUser = new LoginUser(SecurityUtils.securityUser(registerUser),new ArrayList<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        //存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //生成access_token和refresh_token
        Token token = tokenService.createToken(registerUser.getUsername());
        User byId = userService.getById(registerUser.getUserId());

        UserVO userVO = SecurityUtils.securityUserStrict(byId);
        userVO.setLevel(level);

        UserResponse userResponse = new UserResponse(userVO, token);

        return userResponse;
    }


    @Override
    public UserResponse loginByPhone(User user) {
        //TODO

        User securityUser = SecurityUtils.securityUser(user);

        LoginUser loginUser = new LoginUser(securityUser,new ArrayList<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        //存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //生成access_token和refresh_token
        Token token = tokenService.createToken(loginUser.getUsername());

        UserVO userVO = SecurityUtils.securityUserStrict(securityUser);
        //设置等级
        Level level = levelService.getOne(new QueryWrapper<Level>().select("level").eq("user_id", user.getUserId()));
        userVO.setLevel(level.getLevel());

        UserResponse userResponse = new UserResponse(userVO, token);

        return userResponse;
    }
}
