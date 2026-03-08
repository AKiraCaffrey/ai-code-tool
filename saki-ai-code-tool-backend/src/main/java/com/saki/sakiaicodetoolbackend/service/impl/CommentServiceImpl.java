package com.saki.sakiaicodetoolbackend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.saki.sakiaicodetoolbackend.constant.UserConstant;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.mapper.CommentMapper;
import com.saki.sakiaicodetoolbackend.mapper.PostMapper;
import com.saki.sakiaicodetoolbackend.model.dto.comment.CommentCreateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.Comment;
import com.saki.sakiaicodetoolbackend.model.entity.CommentLike;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.CommentVO;
import com.saki.sakiaicodetoolbackend.model.vo.UserVO;
import com.saki.sakiaicodetoolbackend.service.CommentLikeService;
import com.saki.sakiaicodetoolbackend.service.CommentService;
import com.saki.sakiaicodetoolbackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论（支持二级评论） 服务层实现。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private UserService userService;

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentLikeService commentLikeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentCreateRequest request, Long userId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Long postId = request.getPostId();
        String content = request.getContent();
        Long parentCommentId = request.getParentCommentId();
        Long replyUserId = request.getReplyUserId();

        ThrowUtils.throwIf(postId == null, ErrorCode.PARAMS_ERROR, "帖子ID不能为空");
        ThrowUtils.throwIf(content == null || content.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "评论内容不能为空");

        Post post = postMapper.selectOneById(postId);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        if (parentCommentId != null) {
            Comment parentComment = this.getById(parentCommentId);
            ThrowUtils.throwIf(parentComment == null, ErrorCode.NOT_FOUND_ERROR, "父评论不存在");
            ThrowUtils.throwIf(!postId.equals(parentComment.getPostId()), ErrorCode.PARAMS_ERROR, "父评论不属于该帖子");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentCommentId(parentCommentId);
        comment.setReplyUserId(replyUserId);
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setIsDelete(0);

        boolean result = this.save(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建评论失败");

        post.setCommentCount(post.getCommentCount() + 1);
        postMapper.update(post);

        return comment.getId();
    }

    @Override
    public List<CommentVO> getCommentsByPostId(Long postId, Long loginUserId, String sortType) {
        ThrowUtils.throwIf(postId == null, ErrorCode.PARAMS_ERROR, "帖子ID不能为空");

        if (sortType == null || sortType.trim().isEmpty()) {
            sortType = "latest";
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("post_id", postId)
                .isNull("parent_comment_id")
                .eq("is_delete", 0);

        if ("hot".equals(sortType)) {
            queryWrapper.orderBy("like_count", false);
            queryWrapper.orderBy("create_time", false);
        } else {
            queryWrapper.orderBy("create_time", false);
        }

        List<Comment> comments = this.list(queryWrapper);

        return convertToCommentVOList(comments, loginUserId);
    }

    @Override
    public List<CommentVO> getRepliesByCommentId(Long parentCommentId, Long loginUserId) {
        ThrowUtils.throwIf(parentCommentId == null, ErrorCode.PARAMS_ERROR, "父评论ID不能为空");

        List<Comment> replies = this.list(QueryWrapper.create()
                .eq("parent_comment_id", parentCommentId)
                .eq("is_delete", 0)
                .orderBy("create_time", true));

        return convertToCommentVOList(replies, loginUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        ThrowUtils.throwIf(commentId == null, ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Comment comment = this.getById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");

        User loginUser = userService.getById(userId);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isOwner = comment.getUserId().equals(userId);
        ThrowUtils.throwIf(!isAdmin && !isOwner, ErrorCode.NO_AUTH_ERROR, "无权删除该评论");

        comment.setIsDelete(1);
        boolean result = this.updateById(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除评论失败");

        Post post = postMapper.selectOneById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postMapper.update(post);
        }
    }

    private List<CommentVO> convertToCommentVOList(List<Comment> comments, Long loginUserId) {
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Set<Long> replyUserIds = comments.stream()
                .map(Comment::getReplyUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        userIds.addAll(replyUserIds);

        Map<Long, UserVO> userVOMap = getUserVOMap(userIds);

        Set<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toSet());
        Set<Long> likedCommentIds = getLikedCommentIds(commentIds, loginUserId);

        return comments.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            commentVO.setId(comment.getId());
            commentVO.setPostId(comment.getPostId());
            commentVO.setUserId(comment.getUserId());
            commentVO.setUser(userVOMap.get(comment.getUserId()));
            commentVO.setParentCommentId(comment.getParentCommentId());
            commentVO.setReplyUserId(comment.getReplyUserId());
            if (comment.getReplyUserId() != null) {
                commentVO.setReplyUser(userVOMap.get(comment.getReplyUserId()));
            }
            commentVO.setContent(comment.getContent());
            commentVO.setLikeCount(comment.getLikeCount());
            commentVO.setIsLiked(likedCommentIds.contains(comment.getId()));
            commentVO.setCreateTime(comment.getCreateTime());

            if (comment.getParentCommentId() == null) {
                int replyCount = this.getMapper().countByParentCommentId(comment.getId());
                commentVO.setReplyCount(replyCount);
            }

            return commentVO;
        }).collect(Collectors.toList());
    }

    private Map<Long, UserVO> getUserVOMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }
        List<User> users = userService.listByIds(userIds);
        List<UserVO> userVOList = userService.getUserVOList(users);
        return userVOList.stream().collect(Collectors.toMap(UserVO::getId, vo -> vo, (v1, v2) -> v1));
    }

    private Set<Long> getLikedCommentIds(Set<Long> commentIds, Long loginUserId) {
        if (loginUserId == null || commentIds.isEmpty()) {
            return new HashSet<>();
        }
        return commentLikeService.getLikedCommentIds(commentIds, loginUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId, Long loginUserId) {
        ThrowUtils.throwIf(commentId == null || commentId <= 0, ErrorCode.PARAMS_ERROR, "评论ID无效");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Comment comment = this.getById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");

        CommentLike existLike = commentLikeService.getOne(QueryWrapper.create()
                .eq("comment_id", commentId)
                .eq("user_id", loginUserId));

        commentLikeService.like(commentId, loginUserId);

        if (existLike == null || existLike.getIsDelete() == 1) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            this.updateById(comment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeComment(Long commentId, Long loginUserId) {
        ThrowUtils.throwIf(commentId == null || commentId <= 0, ErrorCode.PARAMS_ERROR, "评论ID无效");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Comment comment = this.getById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");

        boolean wasLiked = commentLikeService.isLiked(commentId, loginUserId);
        commentLikeService.unlike(commentId, loginUserId);

        if (wasLiked) {
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            this.updateById(comment);
        }
    }
}
