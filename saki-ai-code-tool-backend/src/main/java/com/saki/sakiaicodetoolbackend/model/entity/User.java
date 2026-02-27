package com.saki.sakiaicodetoolbackend.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 对应数据库表user，存储用户基本信息
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-02-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "user",  schema = "saki_ai_code_tool")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -4463164510585838425L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 账号
     */
    @Column("user_account")
    private String userAccount;

    /**
     * 密码
     */
    @Column("user_password")
    private String userPassword;

    /**
     * 用户昵称
     */
    @Column("username")
    private String userName;

    /**
     * 用户头像
     */
    @Column("user_avatar")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Column("user_profile")
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @Column("user_role")
    private String userRole;

    /**
     * 编辑时间
     */
    @Column("edit_time")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @Column("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "is_delete", isLogicDelete = true)
    private Integer isDelete;

}
