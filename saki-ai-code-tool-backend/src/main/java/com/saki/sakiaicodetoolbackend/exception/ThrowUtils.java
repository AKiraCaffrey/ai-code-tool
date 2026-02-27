package com.saki.sakiaicodetoolbackend.exception;

/**
 * 异常抛出工具类
 * <p>
 * 提供条件判断后抛出异常的便捷方法
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
public class ThrowUtils {

    /**
     * 条件成立则抛出异常
     *
     * @param condition        条件
     * @param runtimeException 运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
