package com.saki.sakiaicodetoolbackend.model.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论点赞请求DTO
 * <p>
 * 用于点赞/取消点赞评论时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class CommentLikeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    private Long commentId;
}
