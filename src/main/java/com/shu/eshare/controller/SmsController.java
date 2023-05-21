package com.shu.eshare.controller;

import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.Constant;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.common.annotation.RateLimit;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.service.SmsService;
import com.shu.eshare.utils.RandomUtil;
import com.shu.eshare.utils.RedisCache;
import com.shu.eshare.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Resource
    public SmsService smsService;

    @Resource
    public RedisCache redisCache;

    @RateLimit(key = "Sms",time = 60,count = 1,ipLimit = true)
    @GetMapping("/sendSms")
    public BaseResponse<String> sendSms(String phone){
        //1.生成uuid
        String uuid = UUID.randomUUID().toString();
        // 拼接redis key
        String codeKey = Constant.PHONE_CODE_KEY + uuid + ":" + phone;
        //2.通过随机数字工具类生成
        String codeValue = RandomUtil.getSixBitRandom();

        Boolean isSend = smsService.sendMessage(phone,codeValue);
        if (!isSend){
            throw new BusinessException(ErrorCode.SMS_ERROR);
        }else {
            //存入Redis中
            redisCache.setCacheObject(codeKey,codeValue,10, TimeUnit.MINUTES);
            return ResultUtils.success(uuid);
        }
    }


}
