package com.shu.eshare.model.response;

import lombok.Data;

@Data
public class GithubAccessToken {
    private String access_token;
    private String token_type;
}
