package com.saki.sakiaicodetoolbackend.controller;

import com.saki.sakiaicodetoolbackend.common.BaseResponse;
import com.saki.sakiaicodetoolbackend.common.ResultUtils;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.model.dto.comment.CommentLikeRequest;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.service.CommentLikeService;
import com.saki.sakiaicodetoolbackend.service.CommentService;
import com.saki.sakiaicodetoolbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 评论点赞 控制层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@RestController
@RequestMapping("/commentLike")
public class CommentLikeController {

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    /**
     * 点赞评论
     * <p>
     * 需要登录，点赞评论
     *
     * @param request 点赞请求参数
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> likeComment(@RequestBody CommentLikeRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(request == null || request.getCommentId() == null, ErrorCode.PARAMS_ERROR, "参数错误");

        commentService.likeComment(request.getCommentId(), loginUser.getId());
        return ResultUtils.success(true);
    }

    /**
     * 取消点赞
     * <p>
     * 需要登录，取消点赞评论
     *
     * @param request 取消点赞请求参数
     * @param httpRequest HTTP请求对象
     * @return 是否成功
     */
    @PostMapping("/unlike")
    public BaseResponse<Boolean> unlikeComment(@RequestBody CommentLikeRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        ThrowUtils.throwIf(request == null || request.getCommentId() == null, ErrorCode.PARAMS_ERROR, "参数错误");

        commentService.unlikeComment(request.getCommentId(), loginUser.getId());
        return ResultUtils.success(true);
    }
}
