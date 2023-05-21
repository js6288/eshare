package com.shu.eshare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Github登录配置类
 */
@Data
@ConfigurationProperties(prefix = "oauth.github")
public class GitHubConstant {

    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String authorizeURL;
    private String accessToken;
    private String userInfo;

}
