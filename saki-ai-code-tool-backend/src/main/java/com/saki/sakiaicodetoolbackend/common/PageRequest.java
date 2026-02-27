package com.saki.sakiaicodetoolbackend.common;

import lombok.Data;

/**
 * 请求封装类
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-25
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}