package com.shu.eshare.service;

import java.util.concurrent.ExecutionException;

public interface SmsService {

    Boolean sendMessage(String phone,String code);

    boolean verifyMessage(String code, String uuid, String phone);
}
