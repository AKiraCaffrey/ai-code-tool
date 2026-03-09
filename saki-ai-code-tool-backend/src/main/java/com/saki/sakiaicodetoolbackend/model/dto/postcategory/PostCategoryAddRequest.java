package com.saki.sakiaicodetoolbackend.model.dto.postcategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子分类创建请求DTO
 * <p>
 * 用于创建新帖子分类时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Data
public class PostCategoryAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 128, message = "分类名称长度不能超过128个字符")
    private String name;

    /**
     * 排序值（可选，默认为0）
     */
    private Integer sortOrder;
}
