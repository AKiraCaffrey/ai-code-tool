package com.saki.sakiaicodetoolbackend.model.dto.postcategory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子分类更新请求DTO
 * <p>
 * 用于更新帖子分类时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Data
public class PostCategoryUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序值
     */
    private Integer sortOrder;
}
