package com.saki.sakiaicodetoolbackend.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 游标分页请求基类
 * <p>
 * 用于游标分页查询的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Data
public class CursorPageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 游标（Base64编码的JSON字符串）
     * 首次加载时不传，后续加载时传入上次返回的nextCursor
     */
    private String cursor;

    /**
     * 页面大小
     * 默认10条，最大50条
     */
    private int pageSize = 10;

    /**
     * 获取有效的页面大小
     * 限制最大值为50，防止一次查询过多数据
     *
     * @return 有效的页面大小
     */
    public int getEffectivePageSize() {
        return Math.min(Math.max(pageSize, 1), 50);
    }
}
