package com.saki.sakiaicodetoolbackend.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 帖子 实体类。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("post")
public class Post implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
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
     * 标题
     */
    private String title;

    /**
     * 首图地址
     */
    private String firstImageUrl;

    /**
     * 帖子内容（富文本HTML）
     */
    private String content;

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
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}
