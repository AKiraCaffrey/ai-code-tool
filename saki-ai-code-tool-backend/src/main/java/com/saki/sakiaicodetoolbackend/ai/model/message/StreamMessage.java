package com.saki.sakiaicodetoolbackend.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式消息响应基类
 * <p>
 * 所有流式消息的父类，定义消息类型字段
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {

    /**
     * 消息类型
     */
    private String type;
}