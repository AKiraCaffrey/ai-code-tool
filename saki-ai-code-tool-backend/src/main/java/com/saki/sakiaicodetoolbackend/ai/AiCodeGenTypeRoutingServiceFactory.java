package com.saki.sakiaicodetoolbackend.ai;

import com.saki.sakiaicodetoolbackend.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI代码生成类型路由服务工厂
 * <p>
 * 用于创建AI代码生成类型路由服务实例，智能选择代码生成类型
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Slf4j
@Configuration
public class AiCodeGenTypeRoutingServiceFactory {

    /**
     * 创建AI代码生成类型路由服务实例
     *
     * @return AI代码生成类型路由服务实例
     */
    public AiCodeGenTypeRoutingService createAiCodeGenTypeRoutingService() {
        ChatModel chatModel = SpringContextUtil.getBean("routingChatModelPrototype", ChatModel.class);
        // 返回一个实现了AiCodeGenTypeRoutingService接口的动态代理对象
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 默认提供一个AI代码生成类型路由服务Bean
     *
     * @return AI代码生成类型路由服务实例
     */
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return createAiCodeGenTypeRoutingService();
    }
}