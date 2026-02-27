package com.saki.sakiaicodetoolbackend.common;

import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应类
 * <p>
 * 统一封装API接口的响应结果，包含状态码、数据和消息
 *
 * @param <T> 数据类型
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class BaseResponse<T> implements Serializable {

    /** 状态码 */
    private int code;

    /** 响应数据 */
    private T data;

    /** 响应消息 */
    private String message;

    /**
     * 构造响应
     *
     * @param code    状态码
     * @param data    响应数据
     * @param message 响应消息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
