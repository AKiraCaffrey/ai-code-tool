package com.saki.sakiaicodetoolbackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子分类视图对象VO
 * <p>
 * 用于前端展示的帖子分类信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Data
public class PostCategoryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序值
     */
    private Integer sortOrder;
}
