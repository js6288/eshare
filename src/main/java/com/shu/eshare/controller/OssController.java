package com.shu.eshare.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.annotation.RateLimit;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.response.OssPolicy;
import com.shu.eshare.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Value("${aliyun.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket}")
    private String bucket;


    @GetMapping("/policy")
    @RateLimit(key = "oss:policy",count = 20,ipLimit = true)
    public BaseResponse policy(){
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
//        String callbackUrl = "http://88.88.88.88:8888";

        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String dir = format+"/"; // 用户上传文件时指定的前缀。

        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        OssPolicy ossPolicy = new OssPolicy();
        try{
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);


            ossPolicy.setAccessKeyId(accessKeyId);
            ossPolicy.setPolicy(encodedPolicy);
            ossPolicy.setSignature(postSignature);
            ossPolicy.setHost(host);
            ossPolicy.setDir(dir);
            ossPolicy.setExpire(String.valueOf(expireEndTime / 1000));

        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.OSS_POLICY_ERROR);
        }finally {
            ossClient.shutdown();
        }
        return ResultUtils.success(ossPolicy);
    }
}
