package com.saki.sakiaicodetoolbackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用更新请求DTO
 * <p>
 * 用于用户更新应用时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class AppUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7653853167178421101L;
    /**
     * id
     */
    private Long id;
    /**
     * 应用名称
     */
    private String appName;
} 