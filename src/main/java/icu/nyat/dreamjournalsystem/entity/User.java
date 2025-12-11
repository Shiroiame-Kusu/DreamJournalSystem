package icu.nyat.dreamjournalsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    @TableField("password_hash")
    private String passwordHash;

    private String nickname;

    private String avatarUrl;

    private UserRole role;

    private UserStatus status;

    private LocalDateTime lastLoginAt;

    private String lastLoginIp;

    private Integer loginAttempts;

    private LocalDateTime lockedUntil;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        USER, ADMIN
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, BANNED
    }
}
