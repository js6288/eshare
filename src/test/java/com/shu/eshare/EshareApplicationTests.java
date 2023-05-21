package com.shu.eshare;

import com.shu.eshare.mapper.UserMapper;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.utils.SecurityUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Resource;

@SpringBootTest
class EshareApplicationTests {

    @Resource
    public UserMapper userMapper;

    @Resource
    public UserDetailsService userDetailsService;

    @Test
    void contextLoads() {

        System.out.println("java");
    }

    @Test
    void testMapperInsert(){
//        User user = new User();
//        user.setUsername("test-user");
//        user.setNickname("test-nickname");
//        user.setPassword(SecurityUtils.encryptPassword("test-password"));
//        int insert = userMapper.insert(user);
//        System.out.println(insert);

//        User user = new User();
//        user.setUsername("test-user1");
//        user.setNickname("test-nickname1");
//        user.setPassword(SecurityUtils.encryptPassword("test-password1"));
//        int insert = userMapper.insert(user);
//        System.out.println(insert);
//        System.out.println(user);

        User user = new User();
        user.setUsername("test-user2");
        user.setNickname("test-nickname2");
        user.setPassword(SecurityUtils.encryptPassword("test-password2"));
        int insert = userMapper.insert(user);
        System.out.println(insert);
        System.out.println(user);
    }

    @Test
    public void userDetailsServiceTest(){
        LoginUser loginUser = (LoginUser)userDetailsService.loadUserByUsername("test-user2");
        System.out.println(loginUser);

    }




}
