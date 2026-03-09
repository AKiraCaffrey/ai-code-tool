package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.model.entity.CommentLike;

import java.util.Set;

/**
 * 评论点赞 服务层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
public interface CommentLikeService extends IService<CommentLike> {

    /**
     * 判断用户是否点赞过评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean isLiked(Long commentId, Long userId);

    /**
     * 点赞评论（使用Upsert原子操作）
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void like(Long commentId, Long userId);

    /**
     * 取消点赞
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void unlike(Long commentId, Long userId);

    /**
     * 批量获取用户已点赞的评论ID
     *
     * @param commentIds 评论ID集合
     * @param userId 用户ID
     * @return 已点赞的评论ID集合
     */
    Set<Long> getLikedCommentIds(Set<Long> commentIds, Long userId);
}
