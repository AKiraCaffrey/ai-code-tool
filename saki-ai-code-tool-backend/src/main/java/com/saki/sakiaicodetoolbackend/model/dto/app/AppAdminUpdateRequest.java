package com.saki.sakiaicodetoolbackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员更新应用请求DTO
 * <p>
 * 用于管理员更新应用时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class AppAdminUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 9202135112996668237L;
    /**
     * id
     */
    private Long id;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用封面
     */
    private String cover;
    /**
     * 优先级
     */
    private Integer priority;
} 