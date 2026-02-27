package com.saki.sakiaicodetoolbackend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户创建请求DTO
 * <p>
 * 用于管理员创建用户时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class UserAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2765431649130021199L;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色: user, admin
     */
    private String userRole;
}