package com.saki.sakiaicodetoolbackend.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 使用自定义线程池替代默认SimpleAsyncTaskExecutor
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Configuration
public class WebMvcAsyncConfig implements WebMvcConfigurer {

    @Resource
    private AsyncTaskExecutor openAiStreamingChatModelTaskExecutor;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(openAiStreamingChatModelTaskExecutor);
    }
}
