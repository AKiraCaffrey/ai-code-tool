package com.saki.sakiaicodetoolbackend.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 自定义业务异常，用于封装业务逻辑中的错误信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final int code;

    /**
     * 构造业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
