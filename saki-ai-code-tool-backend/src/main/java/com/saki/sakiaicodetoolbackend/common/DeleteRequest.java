package com.saki.sakiaicodetoolbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求包装类
 * <p>
 * 用于封装删除操作的请求参数
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 要删除的记录ID */
    private Long id;
}