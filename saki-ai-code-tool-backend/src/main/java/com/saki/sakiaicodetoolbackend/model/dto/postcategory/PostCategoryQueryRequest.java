package com.saki.sakiaicodetoolbackend.model.dto.postcategory;

import com.saki.sakiaicodetoolbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子分类查询请求DTO
 * <p>
 * 用于分页查询帖子分类时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCategoryQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称（模糊搜索）
     */
    private String name;
}
