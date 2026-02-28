package com.saki.sakiaicodetoolbackend.ai.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AI 响应消息
 * <p>
 * 用于封装AI的响应数据
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AiResponseMessage extends StreamMessage {

    /**
     * AI响应数据
     */
    private String data;

    /**
     * 构造函数
     *
     * @param data AI响应数据
     */
    public AiResponseMessage(String data) {
        super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
        this.data = data;
    }
}