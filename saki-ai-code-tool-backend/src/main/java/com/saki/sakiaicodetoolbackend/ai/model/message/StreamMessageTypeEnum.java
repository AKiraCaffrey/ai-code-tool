package com.saki.sakiaicodetoolbackend.ai.model.message;

import lombok.Getter;

/**
 * 流式消息类型枚举
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Getter
public enum StreamMessageTypeEnum {

    AI_RESPONSE("ai_response", "AI响应"),
    TOOL_REQUEST("tool_request", "工具请求"),
    TOOL_EXECUTED("tool_executed", "工具执行结果"),
    BUILD_STATUS("build_status", "构建状态");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举描述文本
     */
    private final String text;

    /**
     * 构造函数
     *
     * @param value 枚举值
     * @param text  枚举描述文本
     */
    StreamMessageTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 枚举值
     * @return 枚举实例，未找到返回null
     */
    public static StreamMessageTypeEnum getEnumByValue(String value) {
        for (StreamMessageTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
