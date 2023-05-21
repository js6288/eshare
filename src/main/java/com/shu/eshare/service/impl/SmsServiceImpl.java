package com.shu.eshare.service.impl;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.google.gson.Gson;
import com.shu.eshare.common.Constant;
import com.shu.eshare.service.SmsService;
import com.shu.eshare.utils.RedisCache;
import darabonba.core.client.ClientOverrideConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${aliyun.access-key-id}")
    public String accessKeyId;

    @Value("${aliyun.access-key-secret}")
    public String accessKeySecret;

    @Resource
    public RedisCache redisCache;


    @Override
    public Boolean sendMessage(String phone,String code){


        Map<String,Object> map = new HashMap<>();
        map.put("code",code);

        Gson gson = new Gson();

        String params = gson.toJson(map);


        //发送给阿里云
        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName("ESHARE")
                .templateCode("SMS_273620474")
                .phoneNumbers(phone)
                .templateParam(params)
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        // Synchronously get the return value of the API request
        try {
            SendSmsResponseBody sendSmsResponseBody = response.get().getBody();
            if ("OK".equals(sendSmsResponseBody.getCode())){
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Finally, close the client
        client.close();

        return false;
    }

    /**
     * 校验手机验证码
     * @param code 手机验证码
     * @param uuid uuid
     * @param phone 手机号
     * @return 验证码正确返回true否则返回false
     */
    @Override
    public boolean verifyMessage(String code,String uuid,String phone){
        String codeKey = Constant.PHONE_CODE_KEY + uuid + ":" + phone;

        String redisCacheCode = redisCache.getCacheObject(codeKey);
        //验证码过期或不存在
        if (redisCacheCode == null){
            return false;
        }
        //验证成功删除redis缓存
        redisCache.deleteObject(codeKey);
        return StringUtils.isNotBlank(code) && code.equals(redisCacheCode);
    }


}
