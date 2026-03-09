package com.saki.sakiaicodetoolbackend.model.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子点赞请求DTO
 * <p>
 * 用于点赞/取消点赞帖子时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-05
 */
@Data
public class PostLikeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;
}
