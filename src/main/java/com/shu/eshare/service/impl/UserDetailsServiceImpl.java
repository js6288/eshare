package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shu.eshare.mapper.UserMapper;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.select("user_id","username","password");
        userQueryWrapper.eq("username",username);
        User user = userMapper.selectOne(userQueryWrapper);
        if (ObjectUtils.isEmpty(user)){
            log.info("登录用户{}不存在",username);
        }

        return new LoginUser(user,new ArrayList<>());
    }
}
