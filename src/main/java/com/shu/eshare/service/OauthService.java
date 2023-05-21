package com.shu.eshare.service;

import com.shu.eshare.model.domain.Oauth;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shu.eshare.model.response.UserResponse;

/**
* @author ljs
* @description 针对表【oauth】的数据库操作Service
* @createDate 2023-03-20 17:52:44
*/
public interface OauthService extends IService<Oauth> {

    UserResponse loginByGitHub(String code);

    Boolean bindGithubAccount(String code);
}
