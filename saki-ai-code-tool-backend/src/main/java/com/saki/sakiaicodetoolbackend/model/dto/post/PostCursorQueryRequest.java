package com.saki.sakiaicodetoolbackend.model.dto.post;

import com.saki.sakiaicodetoolbackend.common.CursorPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子游标分页查询请求
 * <p>
 * 用于帖子列表的游标分页查询，支持分类筛选和关键词搜索
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCursorQueryRequest extends CursorPageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     * 用于按分类筛选帖子
     */
    private Long categoryId;

    /**
     * 关键词
     * 用于搜索帖子标题
     */
    private String keyword;

    /**
     * 排序类型
     * latest-最新（默认），hot-最热
     */
    private String sortType;
}
