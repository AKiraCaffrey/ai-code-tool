package com.saki.sakiaicodetoolbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * OpenAI流式聊天模型配置类
 * <p>
 * 配置OpenAI流式聊天模型的线程池，用于并发处理AI请求
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Configuration
public class OpenAiStreamChatModelConfig {

    /**
     * 创建OpenAI流式聊天模型任务执行器
     *
     * @return 异步任务执行器
     */
    @Bean("openAiStreamingChatModelTaskExecutor")
    AsyncTaskExecutor openAiStreamingChatModelTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("my-LangChain4j-OpenAI-");
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setMaxPoolSize(16);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }

}
