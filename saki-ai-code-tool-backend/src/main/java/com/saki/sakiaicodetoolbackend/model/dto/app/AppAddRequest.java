package com.saki.sakiaicodetoolbackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用创建请求DTO
 * <p>
 * 用于创建新应用时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class AppAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6478983941598130950L;
    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;
} 