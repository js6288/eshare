package com.shu.eshare.service.impl;

import com.shu.eshare.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestServiceImpl implements TestService {


    @Override
    @Async("customAsyncThreadPool")
    public void testAsync() {
        System.out.println("executeAsync");
        log.info("当前运行线程名称:{}", Thread.currentThread().getName());
    }
}
