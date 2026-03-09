package com.saki.sakiaicodetoolbackend.ratelimter.annotation;

import com.saki.sakiaicodetoolbackend.ratelimter.enums.RateLimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * <p>
 * 用于标记需要进行限流控制的接口方法，支持多种限流策略
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流key前缀
     */
    String key() default "";

    /**
     * 每个时间窗口允许的请求数
     */
    int rate() default 10;

    /**
     * 时间窗口（秒）
     */
    int rateInterval() default 1;

    /**
     * 限流类型
     */
    RateLimitType limitType() default RateLimitType.USER;

    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
}
