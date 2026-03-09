package com.saki.sakiaicodetoolbackend.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.saki.sakiaicodetoolbackend.common.CursorPage;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCreateRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCursorQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostUpdateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.vo.PostDetailVO;
import com.saki.sakiaicodetoolbackend.model.vo.PostVO;

/**
 * 帖子 服务层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
public interface PostService extends IService<Post> {

    /**
     * 游标分页查询帖子列表
     *
     * @param request     查询请求参数
     * @param loginUserId 当前登录用户ID（可能为空）
     * @return 游标分页结果
     */
    CursorPage<PostVO> getPostCursorPage(PostCursorQueryRequest request, Long loginUserId);

    /**
     * 创建帖子
     *
     * @param request     创建请求参数
     * @param loginUserId 当前登录用户ID
     * @return 新创建的帖子ID
     */
    Long createPost(PostCreateRequest request, Long loginUserId);

    /**
     * 获取帖子详情
     *
     * @param postId       帖子ID
     * @param loginUserId 当前登录用户ID（可能为空）
     * @return 帖子详情
     */
    PostDetailVO getPostDetail(Long postId, Long loginUserId);

    /**
     * 点赞帖子
     *
     * @param postId      帖子ID
     * @param loginUserId 当前登录用户ID
     */
    void likePost(Long postId, Long loginUserId);

    /**
     * 取消点赞
     *
     * @param postId      帖子ID
     * @param loginUserId 当前登录用户ID
     */
    void unlikePost(Long postId, Long loginUserId);

    /**
     * 更新帖子
     *
     * @param request     更新请求参数
     * @param loginUserId 当前登录用户ID
     * @return 是否更新成功
     */
    boolean updatePost(PostUpdateRequest request, Long loginUserId);

    /**
     * 删除帖子
     *
     * @param postId      帖子ID
     * @param loginUserId 当前登录用户ID
     * @return 是否删除成功
     */
    boolean deletePost(Long postId, Long loginUserId);

    /**
     * 分页查询帖子列表（管理员用）
     *
     * @param request     查询请求参数
     * @param loginUserId 当前登录用户ID
     * @return 分页结果
     */
    Page<PostVO> listPostByPage(PostQueryRequest request, Long loginUserId);

    /**
     * 设置帖子置顶状态
     *
     * @param postId      帖子ID
     * @param isTop       是否置顶（0-否，1-是）
     * @param loginUserId 当前登录用户ID
     * @return 是否设置成功
     */
    boolean setPostTop(Long postId, Integer isTop, Long loginUserId);

}
