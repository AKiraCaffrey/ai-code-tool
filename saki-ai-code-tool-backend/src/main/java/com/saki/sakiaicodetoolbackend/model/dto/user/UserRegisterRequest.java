package com.saki.sakiaicodetoolbackend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求DTO
 * <p>
 * 用于用户注册时的请求参数封装
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -6636104343721689813L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}