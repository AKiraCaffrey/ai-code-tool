package com.saki.sakiaicodetoolbackend.model.dto.post;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子分页查询请求DTO
 * <p>
 * 用于管理员分页查询帖子列表
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-06
 */
@Data
public class PostQueryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 标题（模糊搜索）
     */
    private String title;

    /**
     * 发帖用户ID
     */
    private Long userId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 是否置顶（0-未置顶，1-置顶）
     */
    private Integer isTop;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
