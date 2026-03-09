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
 * 评论（支持二级评论） 实体类。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("comment")
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 父评论id（为空为一级评论）
     */
    private Long parentCommentId;

    /**
     * 被回复用户id
     */
    private Long replyUserId;

    /**
     * 评论内容（富文本HTML）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

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
