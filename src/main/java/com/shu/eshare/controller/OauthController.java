package com.shu.eshare.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.config.GitHubConstant;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.domain.Oauth;
import com.shu.eshare.model.response.UserResponse;
import com.shu.eshare.service.OauthService;
import com.shu.eshare.utils.ResultUtils;
import com.shu.eshare.utils.SecurityUtils;
import com.shu.eshare.utils.UUID;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/oauth")
public class OauthController {

    @Autowired
    private GitHubConstant gitHubConstant;

    @Resource
    private OauthService oauthService;

    /**
     * 获得跳转到GitHub登录页的url
     * @return
     */
    @GetMapping("/getGithubUrl")
    public BaseResponse getGithubUrl(){
        //授权地址
        String authorizeURL = gitHubConstant.getAuthorizeURL();

        //回调地址
        String redirectURI = gitHubConstant.getRedirectURI();

        //用户防止第三方应用csrf攻击
        String uuid = UUID.fastUUID().toString();

        // https://github.com/login/oauth/authorize
        // ?client_id=appid
        // &redirect_uri=redirectUri
        // &state=afasd

        //拼接url
        StringBuilder url = new StringBuilder();
        url.append(authorizeURL);
        url.append("?client_id=").append(gitHubConstant.getClientId());
        url.append("&redirect_uri=").append(redirectURI);
        url.append("&state=").append(uuid);

        return ResultUtils.success(url);
    }

    /**
     * github 登录
     * @param code GitHub回调参数  Http://www.xxx.xxx/githubCallback?code=xyz
     * @return
     */
    @PostMapping("/login/github")
    public BaseResponse loginByGithub(String code){
        if (code.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败");
        }

        //github登录流程
        UserResponse userResponse = oauthService.loginByGitHub(code);
        return ResultUtils.success(userResponse);
    }

    /**
     * 绑定GitHub账号
     * @param code
     * @return
     */
    @PostMapping("/binding/github")
    public BaseResponse bindingGithub(String code){
        if (code.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败");
        }

        //GitHub绑定流程
        Boolean bind = oauthService.bindGithubAccount(code);

        return ResultUtils.success(bind);
    }

    /**
     * 查询当前用户已绑定的第三方平台，根据当前用户userId查询
     * @return
     */
    @GetMapping("/user/binds")
    public BaseResponse getUsersThirdParty(){

        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        List<Oauth> oauthList = oauthService.list(new QueryWrapper<Oauth>().eq("user_id", userId));

        return ResultUtils.success(oauthList);
    }

    /**
     * 第三方账号解绑
     */
    @PostMapping("/unbind")
    public BaseResponse unbind(String authType){
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();

        boolean remove = oauthService.remove(new QueryWrapper<Oauth>().eq("user_id", userId).eq("auth_type", authType));

        return ResultUtils.success(remove);
    }

    //回调地址：http://www.linjsblog.top/qq/callback
    //QQ登录：
    //Step1：获取Authorization Code
    //https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=102045584&redirect_uri=http%3A%2F%2Fwww.linjsblog.top%2Fqq%2Fcallback&state=8884ffid
}
