package com.saki.sakiaicodetoolbackend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新自己信息的请求DTO
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-06
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    
    private String userName;

    
    private String userProfile;

    
    private String userAvatar;
}
