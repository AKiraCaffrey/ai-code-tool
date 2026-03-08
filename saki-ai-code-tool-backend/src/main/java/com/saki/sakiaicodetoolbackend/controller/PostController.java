package com.saki.sakiaicodetoolbackend.controller;

import com.mybatisflex.core.paginate.Page;
import com.saki.sakiaicodetoolbackend.common.BaseResponse;
import com.saki.sakiaicodetoolbackend.common.CursorPage;
import com.saki.sakiaicodetoolbackend.common.ResultUtils;
import com.saki.sakiaicodetoolbackend.constant.UserConstant;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCreateRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCursorQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostLikeRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostTopRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostUpdateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.PostDetailVO;
import com.saki.sakiaicodetoolbackend.model.vo.PostVO;
import com.saki.sakiaicodetoolbackend.service.PostService;
import com.saki.sakiaicodetoolbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 帖子 控制层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    /**
     * 创建帖子
     * <p>
     * 需要登录，创建新帖子
     *
     * @param postCreateRequest 创建请求参数
     * @param httpRequest HTTP请求对象
     * @return 新创建的帖子ID
     */
    @PostMapping("/create")
    public BaseResponse<Long> createPost(@RequestBody PostCreateRequest postCreateRequest, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Long postId = postService.createPost(postCreateRequest, loginUser.getId());
        return ResultUtils.success(postId);
    }

    /**
     * 游标分页查询帖子列表
     * <p>
     * 支持无限滚动加载，管理员、用户、未登录用户均可访问
     *
     * @param request 游标分页查询请求
     * @param httpRequest HTTP请求对象
     * @return 游标分页结果
     */
    @GetMapping("/cursor/list")
    public BaseResponse<CursorPage<PostVO>> getPostCursorPage(
            PostCursorQueryRequest request,
            HttpServletRequest httpRequest) {
        Long loginUserId = null;
        try {
            User loginUser = userService.getLoginUser(httpRequest);
            if (loginUser != null) {
                loginUserId = loginUser.getId();
            }
        } catch (Exception e) {
            // 未登录用户，loginUserId 保持为 null
        }
        CursorPage<PostVO> cursorPage = postService.getPostCursorPage(request, loginUserId);
        return ResultUtils.success(cursorPage);
    }

    /**
     * 获取帖子详情
     * <p>
     * 所有用户均可访问，浏览量+1
     *
     * @param id 帖子ID
     * @param httpRequest HTTP请求对象
     * @return 帖子详情
     */
    @GetMapping("/{id}")
    public BaseResponse<PostDetailVO> getPostDetail(@PathVariable Long id, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");

        Long loginUserId = null;
        try {
            User loginUser = userService.getLoginUser(httpRequest);
            if (loginUser != null) {
                loginUserId = loginUser.getId();
            }
        } catch (Exception e) {
            // 未登录用户，loginUserId 保持为 null
        }

        PostDetailVO postDetail = postService.getPostDetail(id, loginUserId);
        return ResultUtils.success(postDetail);
    }

    /**
     * 点赞帖子
     * <p>
     * 需要登录，点赞帖子
     *
     * @param postLikeRequest 点赞请求参数
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> likePost(@RequestBody PostLikeRequest postLikeRequest, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(postLikeRequest == null || postLikeRequest.getPostId() == null, ErrorCode.PARAMS_ERROR, "参数错误");

        postService.likePost(postLikeRequest.getPostId(), loginUser.getId());
        return ResultUtils.success(true);
    }

    /**
     * 取消点赞
     * <p>
     * 需要登录，取消点赞帖子
     *
     * @param postLikeRequest 取消点赞请求参数
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @PostMapping("/unlike")
    public BaseResponse<Boolean> unlikePost(@RequestBody PostLikeRequest postLikeRequest, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(postLikeRequest == null || postLikeRequest.getPostId() == null, ErrorCode.PARAMS_ERROR, "参数错误");

        postService.unlikePost(postLikeRequest.getPostId(), loginUser.getId());
        return ResultUtils.success(true);
    }

    /**
     * 更新帖子
     * <p>
     * 需要登录，仅帖主和管理员可操作
     *
     * @param postUpdateRequest 更新请求参数
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        boolean result = postService.updatePost(postUpdateRequest, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除帖子
     * <p>
     * 需要登录，仅帖主和管理员可操作
     *
     * @param id 帖子ID
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deletePost(@PathVariable Long id, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");

        boolean result = postService.deletePost(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 分页查询帖子列表（管理员用）
     * <p>
     * 仅管理员可访问
     *
     * @param request     查询请求参数
     * @param httpRequest HTTP请求对象
     * @return 分页结果
     */
    @GetMapping("/admin/list")
    public BaseResponse<Page<PostVO>> listPostByPage(PostQueryRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Page<PostVO> result = postService.listPostByPage(request, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 设置帖子置顶状态
     * <p>
     * 仅管理员可访问
     *
     * @param request     置顶请求参数
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @PutMapping("/admin/top")
    public BaseResponse<Boolean> setPostTop(@RequestBody PostTopRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(request == null || request.getId() == null, ErrorCode.PARAMS_ERROR, "参数错误");

        boolean result = postService.setPostTop(request.getId(), request.getIsTop(), loginUser.getId());
        return ResultUtils.success(result);
    }
}
