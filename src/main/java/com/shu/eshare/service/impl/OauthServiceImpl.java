package com.shu.eshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.config.GitHubConstant;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Level;
import com.shu.eshare.model.domain.Oauth;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.response.GitHubUser;
import com.shu.eshare.model.response.GithubAccessToken;
import com.shu.eshare.model.response.Token;
import com.shu.eshare.model.response.UserResponse;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.model.vo.UserVO;
import com.shu.eshare.service.LevelService;
import com.shu.eshare.service.LoginService;
import com.shu.eshare.service.OauthService;
import com.shu.eshare.mapper.OauthMapper;
import com.shu.eshare.service.UserService;
import com.shu.eshare.utils.IdWorker;
import com.shu.eshare.utils.SecurityUtils;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
* @author ljs
* @description 针对表【oauth】的数据库操作Service实现
* @createDate 2023-03-20 17:52:44
*/
@Service
public class OauthServiceImpl extends ServiceImpl<OauthMapper, Oauth>
    implements OauthService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private GitHubConstant gitHubConstant;

    @Resource
    private UserService userService;

    @Resource
    private LoginService loginService;

    @Resource
    private LevelService levelService;

    @Override
    @Transactional
    public UserResponse loginByGitHub(String code) {
        //获取access_token
        String accessToken = getAccessToken(code);
        //根据access_token获取用户信息
        GitHubUser gitHubUser = getGitHubUser(accessToken);

        //获取openId 转成String 因为数据库存储的是varchar
        String openid = gitHubUser.getId().toString();

        //判断GitHub账号是否已经绑定
        //select * from oauth where open_id = xxx and auth_type = GITHUB
        Oauth oauth = this.getOne(new QueryWrapper<Oauth>().eq("open_id", openid).eq("auth_type", Constant.THIRD_PARTY_GITHUB));
        if (oauth != null){
            //执行登录流程
            Long userId = oauth.getUserId();
            User user = userService.getById(userId);
            //和手机号登录逻辑基本一致，所以就直接用了
            return loginService.loginByPhone(user);
        }
        //执行注册流程
        UserResponse userResponse = registerByGitHub(gitHubUser);

        return userResponse;

    }

    /**
     * 绑定github账号
     * @param code
     * @return
     */
    @Override
    @Transactional
    public Boolean bindGithubAccount(String code) {
        //获取access_token
        String accessToken = getAccessToken(code);
        //根据access_token获取用户信息
        GitHubUser gitHubUser = getGitHubUser(accessToken);

        //获取openId 转成String 因为数据库存储的是varchar
        String openid = gitHubUser.getId().toString();

        //绑定GitHub账号
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        //判断用户是否已经绑定Github
        //select * from oauth where open_id = xxx and auth_type = GITHUB
        Oauth oauth = this.getOne(new QueryWrapper<Oauth>().eq("open_id", openid).eq("auth_type", Constant.THIRD_PARTY_GITHUB));
        if (oauth != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前账号已经被绑定");
        }
        oauth = new Oauth();
        oauth.setUserId(userId);
        oauth.setAuthType(Constant.THIRD_PARTY_GITHUB);
        oauth.setOpenId(openid);
        //保存oauth
        return this.save(oauth);
    }

    @Transactional
    public UserResponse registerByGitHub(GitHubUser gitHubUser){
        //创建新用户
        User user = new User();
        IdWorker idWorker = new IdWorker(1, 1, 1);
        long username_suffix = idWorker.nextId();
        user.setUsername(gitHubUser.getLogin()+username_suffix);
        user.setNickname(gitHubUser.getLogin());
        user.setAccumulatePoints(200L);
        user.setAvatarUrl(gitHubUser.getAvatarUrl());
        user.setPersonalSignature(Constant.PERSONAL_SIGNATURE);
        //随机生成密码
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));

        //保存用户
        userService.save(user);

        //保存用户等级信息
        Level level = new Level();
        level.setUserId(user.getUserId());
        level.setLevel(1);
        levelService.save(level);

        //保存用户与oauth 的关联关系
        Oauth oauth = new Oauth();
        oauth.setAuthType(Constant.THIRD_PARTY_GITHUB);
        oauth.setOpenId(gitHubUser.getId().toString());
        oauth.setUserId(user.getUserId());
        this.save(oauth);

        //生成UserResponse
        return loginService.generateLoginUser(user,level.getLevel());

    }

    public String getAccessToken(String code){
        //获取access_token
        //https://github.com/login/oauth/access_token
        String accessTokenURL = gitHubConstant.getAccessToken();
        //构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        //构造请求体
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("client_id", gitHubConstant.getClientId());
        params.add("client_secret", gitHubConstant.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", gitHubConstant.getRedirectURI());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromHttpUrl(accessTokenURL)
                .queryParams(params)
                .build()
                .toUriString();

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        Gson gson = new Gson();

        GithubAccessToken githubAccessToken = gson.fromJson(exchange.getBody(), GithubAccessToken.class);
        return githubAccessToken.getAccess_token();
    }

    public GitHubUser getGitHubUser(String accessToken){
        String userInfoURL = gitHubConstant.getUserInfo();
        HttpHeaders headers = new HttpHeaders();
        //请求头设置access_token
        headers.set("Authorization", Constant.TOKEN_PREFIX+accessToken);

        HttpEntity<GitHubUser> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<GitHubUser> exchange = restTemplate.exchange(userInfoURL, HttpMethod.GET, httpEntity, GitHubUser.class);

        return exchange.getBody();
    }
}




