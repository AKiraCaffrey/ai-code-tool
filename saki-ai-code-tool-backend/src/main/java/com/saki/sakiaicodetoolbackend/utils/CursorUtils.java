package com.saki.sakiaicodetoolbackend.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 游标工具类
 * <p>
 * 用于游标的编码和解码，支持游标分页功能
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@Slf4j
public class CursorUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CursorUtils() {
    }

    /**
     * 编码游标
     * 将创建时间和ID封装为JSON并进行Base64编码
     *
     * @param createTime 创建时间
     * @param id         记录ID
     * @return Base64编码的游标字符串
     */
    public static String encodeCursor(LocalDateTime createTime, Long id) {
        return encodeCursor(createTime, id, null);
    }

    /**
     * 编码游标（支持热度排序）
     * 将创建时间、ID和热度值封装为JSON并进行Base64编码
     *
     * @param createTime 创建时间
     * @param id         记录ID
     * @param hotScore   热度值（可为null）
     * @return Base64编码的游标字符串
     */
    public static String encodeCursor(LocalDateTime createTime, Long id, Long hotScore) {
        if (createTime == null || id == null) {
            return null;
        }
        try {
            CursorData cursorData = new CursorData();
            cursorData.setCreateTime(createTime.format(DATE_TIME_FORMATTER));
            cursorData.setId(id);
            cursorData.setHotScore(hotScore);
            String json = OBJECT_MAPPER.writeValueAsString(cursorData);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            log.error("编码游标失败", e);
            return null;
        }
    }

    /**
     * 解码游标
     * 将Base64编码的游标字符串解码为创建时间和ID
     *
     * @param cursor Base64编码的游标字符串
     * @return 游标数据对象，解码失败返回null
     */
    public static CursorData decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(cursor);
            String json = new String(bytes, StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(json, CursorData.class);
        } catch (IllegalArgumentException e) {
            log.warn("无效的游标格式: {}", cursor);
            return null;
        } catch (Exception e) {
            log.warn("解码游标失败: {}", cursor);
            return null;
        }
    }

    /**
     * 游标数据内部类
     */
    @Data
    public static class CursorData {
        /**
         * 创建时间字符串
         */
        private String createTime;

        /**
         * 记录ID
         */
        private Long id;

        /**
         * 热度值（用于热度排序）
         */
        private Long hotScore;

        /**
         * 获取解析后的创建时间
         *
         * @return LocalDateTime
         */
        @JsonIgnore
        public LocalDateTime getParsedCreateTime() {
            if (createTime == null) {
                return null;
            }
            return LocalDateTime.parse(createTime, DATE_TIME_FORMATTER);
        }
    }
}
