package com.saki.sakiaicodetoolbackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 帖子详情视图对象VO
 * <p>
 * 用于前端展示的帖子详情信息，包含完整内容和用户信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class PostDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子id
     */
    private Long id;

    /**
     * 发帖用户id
     */
    private Long userId;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标题
     */
    private String title;

    /**
     * 帖子内容（完整HTML）
     */
    private String content;

    /**
     * 首图地址
     */
    private String firstImageUrl;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 发帖用户信息
     */
    private UserVO user;

    /**
     * 当前用户是否点赞
     * 未登录时返回false
     */
    private Boolean isLiked;
}
