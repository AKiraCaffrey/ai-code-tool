package com.saki.sakiaicodetoolbackend.core.handler;

import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.enums.CodeGenTypeEnum;
import com.saki.sakiaicodetoolbackend.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 流处理器执行器
 * <p>
 * 根据代码生成类型创建合适的流处理器：
 * <ul>
 *     <li>传统的Flux流（HTML、MULTI_FILE）-> SimpleTextStreamHandler</li>
 *     <li>TokenStream格式的复杂流（VUE_PROJECT）-> JsonMessageStreamHandler</li>
 * </ul>
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Slf4j
@Component
public class StreamHandlerExecutor {

    @Resource
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    /**
     * 创建流处理器并处理聊天历史记录
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @param codeGenType        代码生成类型
     * @return 处理后的流
     */
    public Flux<String> doExecute(Flux<String> originFlux,
                                  ChatHistoryService chatHistoryService,
                                  long appId, User loginUser, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            // 使用注入的组件实例
            case VUE_PROJECT ->
                    jsonMessageStreamHandler.handle(originFlux, chatHistoryService, appId, loginUser);
            // 简单文本处理器不需要依赖注入
            case HTML, MULTI_FILE ->
                    new SimpleTextStreamHandler().handle(originFlux, chatHistoryService, appId, loginUser);
        };
    }
}
