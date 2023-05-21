package com.shu.eshare;

import com.shu.eshare.config.GitHubConstant;
import com.shu.eshare.config.TaskThreadPoolConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.shu.eshare.mapper") //扫描mapper
@EnableAsync //开启异步线程支持
@EnableScheduling
@EnableConfigurationProperties({TaskThreadPoolConfig.class, GitHubConstant.class}) //开启配置属性支持
public class EshareApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshareApplication.class, args);
    }

}
