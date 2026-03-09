package com.saki.sakiaicodetoolbackend.model.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子置顶请求DTO
 * <p>
 * 用于管理员设置帖子置顶状态
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-06
 */
@Data
public class PostTopRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long id;

    /**
     * 是否置顶（0-取消置顶，1-置顶）
     */
    @NotNull(message = "置顶状态不能为空")
    private Integer isTop;
}
