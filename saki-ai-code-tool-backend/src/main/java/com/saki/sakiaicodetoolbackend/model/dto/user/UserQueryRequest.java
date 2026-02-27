package com.saki.sakiaicodetoolbackend.model.dto.user;

import com.saki.sakiaicodetoolbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户查询请求DTO
 * <p>
 * 用于查询用户列表时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2512168925642546575L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}