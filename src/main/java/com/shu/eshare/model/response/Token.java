package com.shu.eshare.model.response;

import lombok.Data;

@Data
public class Token {
    public String accessToken;
    public String refreshToken;
}
