package com.saki.sakiaicodetoolbackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新应用请求
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