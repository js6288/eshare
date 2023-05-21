package com.shu.eshare.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    //限流唯一标示
    String key() default "";

    //限流单位时间（单位为s）
    int time() default 1;

    //单位时间内限制的访问次数
    int count();

    //是否限制ip
    boolean ipLimit() default false;

}
