package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.model.dto.comment.CommentCreateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.Comment;
import com.saki.sakiaicodetoolbackend.model.vo.CommentVO;

import java.util.List;

/**
 * 评论（支持二级评论） 服务层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
public interface CommentService extends IService<Comment> {

    /**
     * 创建评论
     *
     * @param request 创建请求
     * @param userId 用户ID
     * @return 评论ID
     */
    Long createComment(CommentCreateRequest request, Long userId);

    /**
     * 获取帖子的一级评论列表
     *
     * @param postId 帖子ID
     * @param loginUserId 登录用户ID（可为null）
     * @param sortType 排序类型（latest/hot，默认latest）
     * @return 评论列表
     */
    List<CommentVO> getCommentsByPostId(Long postId, Long loginUserId, String sortType);

    /**
     * 获取评论的二级回复列表
     *
     * @param parentCommentId 父评论ID
     * @param loginUserId 登录用户ID（可为null）
     * @return 回复列表
     */
    List<CommentVO> getRepliesByCommentId(Long parentCommentId, Long loginUserId);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param loginUserId 当前登录用户ID
     */
    void likeComment(Long commentId, Long loginUserId);

    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @param loginUserId 当前登录用户ID
     */
    void unlikeComment(Long commentId, Long loginUserId);
}
