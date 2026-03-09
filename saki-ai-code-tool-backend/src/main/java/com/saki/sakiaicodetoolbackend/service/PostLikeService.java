package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.model.entity.PostLike;

/**
 * 帖子点赞 服务层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
public interface PostLikeService extends IService<PostLike> {

    /**
     * 判断用户是否点赞过帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean isLiked(Long postId, Long userId);

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void like(Long postId, Long userId);

    /**
     * 取消点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void unlike(Long postId, Long userId);
}
