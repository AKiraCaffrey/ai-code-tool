package com.saki.sakiaicodetoolbackend.model.dto.comment;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论查询请求DTO
 * <p>
 * 用于查询评论时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class CommentQueryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 父评论ID（可选，用于查询二级评论）
     */
    private Long parentCommentId;

    /**
     * 排序类型（可选，默认latest）
     * latest: 按创建时间倒序（最新）
     * hot: 按点赞数倒序（最热）
     */
    private String sortType;
}
