package com.saki.sakiaicodetoolbackend.ratelimter.enums;

/**
 * 限流类型枚举
 * <p>
 * 定义不同级别的限流策略
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
public enum RateLimitType {

    /**
     * 接口级别限流
     */
    API,

    /**
     * 用户级别限流
     */
    USER,

    /**
     * IP级别限流
     */
    IP
}