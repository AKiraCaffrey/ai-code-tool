package com.saki.sakiaicodetoolbackend.ai.model.message;

import dev.langchain4j.service.tool.ToolExecution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 工具执行结果消息
 * <p>
 * 用于封装工具执行的完整结果信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ToolExecutedMessage extends StreamMessage {

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
     * 工具执行结果
     */
    private String result;

    /**
     * 构造函数
     *
     * @param toolExecution 工具执行对象
     */
    public ToolExecutedMessage(ToolExecution toolExecution) {
        super(StreamMessageTypeEnum.TOOL_EXECUTED.getValue());
        this.id = toolExecution.request().id();
        this.name = toolExecution.request().name();
        this.arguments = toolExecution.request().arguments();
        this.result = toolExecution.result();
    }
}