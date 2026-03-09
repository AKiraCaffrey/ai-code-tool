package com.saki.sakiaicodetoolbackend.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论创建请求DTO
 * <p>
 * 用于创建评论时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class CommentCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /**
     * 父评论ID（为空表示一级评论）
     */
    private Long parentCommentId;

    /**
     * 被回复用户ID（可选）
     */
    private Long replyUserId;

    /**
     * 评论内容（富文本HTML）
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
