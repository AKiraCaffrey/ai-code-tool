package com.saki.sakiaicodetoolbackend.ai.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 构建状态消息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BuildStatusMessage extends StreamMessage {

    /**
     * 状态值
     */
    private String status;

    /**
     * 状态描述信息
     */
    private String message;

    /**
     * 进度百分比（可选）
     */
    private Integer progress;

    public BuildStatusMessage(String status, String message) {
        super(StreamMessageTypeEnum.BUILD_STATUS.getValue());
        this.status = status;
        this.message = message;
    }

    public BuildStatusMessage(String status, String message, Integer progress) {
        super(StreamMessageTypeEnum.BUILD_STATUS.getValue());
        this.status = status;
        this.message = message;
        this.progress = progress;
    }
}
