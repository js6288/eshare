package com.shu.eshare.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OssPolicy {

    private String accessKeyId;

    private String policy;

    private String signature;

    private String dir;

    private String host;

    private String expire;
}
