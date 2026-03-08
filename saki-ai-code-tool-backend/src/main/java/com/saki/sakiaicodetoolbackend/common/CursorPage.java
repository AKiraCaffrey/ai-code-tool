package com.saki.sakiaicodetoolbackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 游标分页响应类
 * <p>
 * 用于游标分页的统一响应格式，支持无限滚动加载
 *
 * @param <T> 数据类型
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorPage<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 下一页游标（Base64编码的JSON字符串）
     */
    private String nextCursor;

    /**
     * 是否有更多数据
     */
    private Boolean hasMore;
}
