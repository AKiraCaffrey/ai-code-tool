package com.saki.sakiaicodetoolbackend.core.builder;

import lombok.Getter;

/**
 * Vue项目构建状态枚举
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-27
 */
@Getter
public enum BuildStatusEnum {

    INSTALLING("installing", "正在安装依赖"),
    BUILDING("building", "正在构建项目"),
    COMPLETED("completed", "构建完成"),
    FAILED("failed", "构建失败");

    private final String value;

    private final String text;

    BuildStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 状态值
     * @return 枚举实例，未找到返回null
     */
    public static BuildStatusEnum getEnumByValue(String value) {
        for (BuildStatusEnum statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
