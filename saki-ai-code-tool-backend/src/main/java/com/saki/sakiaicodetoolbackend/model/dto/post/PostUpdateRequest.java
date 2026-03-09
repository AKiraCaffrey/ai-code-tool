package com.saki.sakiaicodetoolbackend.model.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子更新请求DTO
 * <p>
 * 用于更新帖子时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class PostUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 512, message = "标题长度不能超过512个字符")
    private String title;

    /**
     * 内容（HTML格式）
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
}
