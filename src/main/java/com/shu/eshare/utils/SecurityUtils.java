package com.shu.eshare.utils;

import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.model.vo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"获取用户信息异常");
        }
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 用户信息弱脱敏，只删除密码，适用于 当前用户信息
     */
    public static User securityUser(User user){

        user.setPassword("");
        return user;
    }

    /**
     * 用户信息强脱敏，适合暴露给其他用户
     */
    public static UserVO securityUserStrict(User user){
        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setGender(user.getGender());
        userVO.setLikesNum(user.getLikesNum());
        userVO.setUserRole(user.getUserRole());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setNickname(user.getNickname());
        userVO.setUsername(user.getUsername());
        userVO.setSchool(user.getSchool());
        userVO.setReadNum(user.getReadNum());
        userVO.setStatus(user.getStatus());
        userVO.setPersonalSignature(user.getPersonalSignature());
        // TODO IP地址暂时写死
        userVO.setIpAddress("广东");


        return userVO;
    }
}
