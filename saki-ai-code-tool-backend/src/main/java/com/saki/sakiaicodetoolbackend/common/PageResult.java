package com.saki.sakiaicodetoolbackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页类
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private List<T> records;

    private long total;

    private long pageSize;

    private long pageNum;
}
