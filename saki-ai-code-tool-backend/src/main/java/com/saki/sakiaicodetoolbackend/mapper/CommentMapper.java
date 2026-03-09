package com.saki.sakiaicodetoolbackend.mapper;

import com.mybatisflex.core.BaseMapper;
import com.saki.sakiaicodetoolbackend.model.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评论（支持二级评论） 映射层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 统计帖子的评论数（包括一级和二级评论）
     *
     * @param postId 帖子ID
     * @return 评论数
     */
    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId} AND is_delete = 0")
    int countByPostId(@Param("postId") Long postId);

    /**
     * 统计评论的回复数（二级评论）
     *
     * @param parentCommentId 父评论ID
     * @return 回复数
     */
    @Select("SELECT COUNT(*) FROM comment WHERE parent_comment_id = #{parentCommentId} AND is_delete = 0")
    int countByParentCommentId(@Param("parentCommentId") Long parentCommentId);
}
