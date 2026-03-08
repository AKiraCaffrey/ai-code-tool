package com.saki.sakiaicodetoolbackend.controller;

import com.saki.sakiaicodetoolbackend.common.BaseResponse;
import com.saki.sakiaicodetoolbackend.common.ResultUtils;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.model.dto.comment.CommentCreateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.CommentVO;
import com.saki.sakiaicodetoolbackend.service.CommentService;
import com.saki.sakiaicodetoolbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论（支持二级评论） 控制层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    /**
     * 创建评论
     * <p>
     * 需要登录，创建新评论
     *
     * @param request 创建请求参数
     * @param httpRequest HTTP请求对象
     * @return 新创建的评论ID
     */
    @PostMapping("/create")
    public BaseResponse<Long> createComment(@RequestBody CommentCreateRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Long commentId = commentService.createComment(request, loginUser.getId());
        return ResultUtils.success(commentId);
    }

    /**
     * 获取帖子的一级评论列表
     * <p>
     * 所有用户均可访问
     *
     * @param postId 帖子ID
     * @param sortType 排序类型（latest/hot，默认latest）
     * @param httpRequest HTTP请求对象
     * @return 评论列表
     */
    @GetMapping("/list/{postId}")
    public BaseResponse<List<CommentVO>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(required = false, defaultValue = "latest") String sortType,
            HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");

        Long loginUserId = null;
        try {
            User loginUser = userService.getLoginUser(httpRequest);
            if (loginUser != null) {
                loginUserId = loginUser.getId();
            }
        } catch (Exception e) {
            // 未登录用户，loginUserId 保持为 null
        }

        List<CommentVO> comments = commentService.getCommentsByPostId(postId, loginUserId, sortType);
        return ResultUtils.success(comments);
    }

    /**
     * 获取评论的二级回复列表
     * <p>
     * 所有用户均可访问
     *
     * @param parentCommentId 父评论ID
     * @param httpRequest HTTP请求对象
     * @return 回复列表
     */
    @GetMapping("/replies/{parentCommentId}")
    public BaseResponse<List<CommentVO>> getRepliesByCommentId(@PathVariable Long parentCommentId, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(parentCommentId == null || parentCommentId <= 0, ErrorCode.PARAMS_ERROR, "父评论ID无效");

        Long loginUserId = null;
        try {
            User loginUser = userService.getLoginUser(httpRequest);
            if (loginUser != null) {
                loginUserId = loginUser.getId();
            }
        } catch (Exception e) {
            // 未登录用户，loginUserId 保持为 null
        }

        List<CommentVO> replies = commentService.getRepliesByCommentId(parentCommentId, loginUserId);
        return ResultUtils.success(replies);
    }

    /**
     * 删除评论
     * <p>
     * 需要登录，只能删除自己的评论
     *
     * @param id 评论ID
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteComment(@PathVariable Long id, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "评论ID无效");

        commentService.deleteComment(id, loginUser.getId());
        return ResultUtils.success(true);
    }
}
