package com.saki.sakiaicodetoolbackend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.mapper.PostLikeMapper;
import com.saki.sakiaicodetoolbackend.mapper.PostMapper;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.entity.PostLike;
import com.saki.sakiaicodetoolbackend.service.PostLikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帖子点赞 服务层实现。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

    @Resource
    private PostMapper postMapper;

    @Override
    public boolean isLiked(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }
        long count = this.count(QueryWrapper.create()
                .eq("post_id", postId)
                .eq("user_id", userId)
                .eq("is_delete", 0));
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Long postId, Long userId) {
        ThrowUtils.throwIf(postId == null || userId == null, ErrorCode.PARAMS_ERROR, "参数错误");

        Post post = postMapper.selectOneById(postId);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);

        this.getMapper().insertOrUpdate(postLike);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(Long postId, Long userId) {
        ThrowUtils.throwIf(postId == null || userId == null, ErrorCode.PARAMS_ERROR, "参数错误");

        Post post = postMapper.selectOneById(postId);
        ThrowUtils.throwIf(post == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        this.getMapper().softDelete(postId, userId);
    }
}
