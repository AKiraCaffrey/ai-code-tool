package com.saki.sakiaicodetoolbackend.mapper;

import com.mybatisflex.core.BaseMapper;
import com.saki.sakiaicodetoolbackend.model.entity.PostLike;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子点赞 映射层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
public interface PostLikeMapper extends BaseMapper<PostLike> {

    /**
     * Upsert操作：点赞或取消点赞后重新点赞
     * 使用ON DUPLICATE KEY UPDATE避免唯一索引冲突
     *
     * @param postLike 点赞记录
     * @return 影响行数
     */
    @Insert("INSERT INTO post_like (post_id, user_id, create_time, is_delete) " +
            "VALUES (#{postId}, #{userId}, NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE " +
            "is_delete = 0, " +
            "create_time = NOW()")
    int insertOrUpdate(PostLike postLike);

    /**
     * 取消点赞（软删除）
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int softDelete(@Param("postId") Long postId, @Param("userId") Long userId);
}
