package com.saki.sakiaicodetoolbackend.ai.model.message;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 工具调用消息
 * <p>
 * 用于封装工具调用的请求信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ToolRequestMessage extends StreamMessage {

    /**
     * 工具请求ID
     */
    private String id;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具参数（JSON格式）
     */
    private String arguments;

    /**
     * 构造函数
     *
     * @param toolExecutionRequest 工具执行请求
     */
    public ToolRequestMessage(ToolExecutionRequest toolExecutionRequest) {
        super(StreamMessageTypeEnum.TOOL_REQUEST.getValue());
        this.id = toolExecutionRequest.id();
        this.name = toolExecutionRequest.name();
        this.arguments = toolExecutionRequest.arguments();
    }
}