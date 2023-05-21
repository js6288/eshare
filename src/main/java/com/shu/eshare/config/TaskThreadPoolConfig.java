package com.shu.eshare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程配置属性类
 */
@Data
@ConfigurationProperties(prefix = "task.pool")
public class TaskThreadPoolConfig {

    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveSeconds;
    private int queueCapacity;
    private int awaitTerminationSeconds;
    private String threadNamePrefix;
}
