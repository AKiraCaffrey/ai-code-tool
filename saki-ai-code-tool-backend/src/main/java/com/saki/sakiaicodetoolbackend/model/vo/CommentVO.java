package com.saki.sakiaicodetoolbackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论视图对象VO
 * <p>
 * 用于前端展示的评论信息，包含用户信息和点赞状态
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class CommentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 评论用户信息
     */
    private UserVO user;

    /**
     * 父评论ID
     */
    private Long parentCommentId;

    /**
     * 被回复用户ID
     */
    private Long replyUserId;

    /**
     * 被回复用户信息
     */
    private UserVO replyUser;

    /**
     * 评论内容（富文本HTML）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 当前用户是否点赞
     * 未登录时返回false
     */
    private Boolean isLiked;

    /**
     * 回复数量（仅一级评论）
     */
    private Integer replyCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
