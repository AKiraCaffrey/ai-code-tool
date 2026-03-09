package com.saki.sakiaicodetoolbackend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.mapper.CommentLikeMapper;
import com.saki.sakiaicodetoolbackend.mapper.CommentMapper;
import com.saki.sakiaicodetoolbackend.model.entity.Comment;
import com.saki.sakiaicodetoolbackend.model.entity.CommentLike;
import com.saki.sakiaicodetoolbackend.service.CommentLikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 评论点赞 服务层实现。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@Service
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLike> implements CommentLikeService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public boolean isLiked(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            return false;
        }
        long count = this.count(QueryWrapper.create()
                .eq("comment_id", commentId)
                .eq("user_id", userId)
                .eq("is_delete", 0));
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Long commentId, Long userId) {
        ThrowUtils.throwIf(commentId == null || userId == null, ErrorCode.PARAMS_ERROR, "参数错误");

        Comment comment = commentMapper.selectOneById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");

        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);

        this.getMapper().insertOrUpdate(commentLike);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(Long commentId, Long userId) {
        ThrowUtils.throwIf(commentId == null || userId == null, ErrorCode.PARAMS_ERROR, "参数错误");

        Comment comment = commentMapper.selectOneById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");

        this.getMapper().softDelete(commentId, userId);
    }

    @Override
    public Set<Long> getLikedCommentIds(Set<Long> commentIds, Long userId) {
        if (userId == null || commentIds.isEmpty()) {
            return new HashSet<>();
        }
        List<CommentLike> commentLikes = this.list(QueryWrapper.create()
                .in("comment_id", commentIds)
                .eq("user_id", userId)
                .eq("is_delete", 0));
        return commentLikes.stream().map(CommentLike::getCommentId).collect(java.util.stream.Collectors.toSet());
    }
}
