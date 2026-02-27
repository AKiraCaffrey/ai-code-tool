package com.saki.sakiaicodetoolbackend.config;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis对话记忆存储配置类
 * <p>
 * 配置Redis作为对话记忆的持久化存储，用于保存AI对话上下文
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisChatMemoryStoreConfig {

    /** Redis主机地址 */
    private String host;

    /** Redis端口 */
    private int port;

    /** Redis密码 */
    private String password;

    /** 过期时间（秒） */
    private long ttl;

    /** Redis用户名 */
    private String username;

    /**
     * 创建Redis对话记忆存储Bean
     *
     * @return Redis对话记忆存储实例
     */
    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        RedisChatMemoryStore.Builder builder = RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .password(password)
                .user(username)
                .ttl(ttl);
        if (StrUtil.isNotBlank(password)) {
            builder.user("default");
        }
        return builder.build();
    }
}
