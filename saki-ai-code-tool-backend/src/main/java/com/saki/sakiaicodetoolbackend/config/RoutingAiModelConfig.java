package com.saki.sakiaicodetoolbackend.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 智能路由AI模型配置类
 * <p>
 * 配置用于智能路由的AI模型，支持动态选择不同的AI模型
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.routing-chat-model")
@Data
public class RoutingAiModelConfig {

    /** API基础URL */
    private String baseUrl;

    /** API密钥 */
    private String apiKey;

    /** 模型名称 */
    private String modelName;

    /** 最大Token数 */
    private Integer maxTokens;

    /** 温度参数 */
    private Double temperature;

    /** 是否记录请求日志 */
    private Boolean logRequests = false;

    private Boolean logResponses = false;

    /**
     * 创建用于路由判断的ChatModel
     */
    @Bean
    @Scope("prototype")
    public ChatModel routingChatModelPrototype() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }
}