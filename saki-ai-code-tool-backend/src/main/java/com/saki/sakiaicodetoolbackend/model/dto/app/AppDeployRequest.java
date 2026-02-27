package com.saki.sakiaicodetoolbackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用部署请求DTO
 * <p>
 * 用于应用部署时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class AppDeployRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6271772651338823055L;
    /**
     * 应用 id
     */
    private Long appId;
}