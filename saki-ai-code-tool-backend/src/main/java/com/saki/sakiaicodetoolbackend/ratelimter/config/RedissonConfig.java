package com.saki.sakiaicodetoolbackend.ratelimter.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 * <p>
 * 配置 Redisson 客户端连接 Redis，用于分布式限流等功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Configuration
public class RedissonConfig {

    /**
     * Redis 主机地址
     */
    @Value("${spring.data.redis.host}")
    private String redisHost;

    /**
     * Redis 端口
     */
    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    /**
     * Redis 密码
     */
    @Value("${spring.data.redis.password}")
    private String redisPassword;

    /**
     * Redis 数据库索引
     */
    @Value("${spring.data.redis.database}")
    private Integer redisDatabase;

    /**
     * 创建 Redisson 客户端
     *
     * @return Redisson 客户端实例
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = "redis://" + redisHost + ":" + redisPort;
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisDatabase)
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(10)
                .setIdleConnectionTimeout(30000)
                .setConnectTimeout(5000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        // 如果有密码则设置密码
        if (redisPassword != null && !redisPassword.isEmpty()) {
            singleServerConfig.setPassword(redisPassword);
        }
        return Redisson.create(config);
    }
}