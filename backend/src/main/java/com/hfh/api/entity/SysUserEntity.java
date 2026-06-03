package com.hfh.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户表实体
 */
@Data
@TableName("sys_user")
@Schema(description = "用户表实体")
public class SysUserEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID（雪花算法）", example = "1234567890123456789")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 密码（加密存储） */
    @Schema(description = "密码（加密存储）", example = "123456")
    private String password;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /** 昵称 */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /** 头像地址 */
    @Schema(description = "头像地址", example = "https://example.com/avatar.jpg")
    private String avatar;

    /** 手机号 */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 状态：0-禁用 1-正常 */
    @Schema(description = "状态：0-禁用 1-正常", example = "1")
    private Integer status;

    /** 角色：0-普通用户 1-管理员 */
    @Schema(description = "角色：0-普通用户 1-管理员", example = "1")
    private Integer role;

    @TableLogic
    @Schema(description = "逻辑删除：0-未删除 1-已删除", example = "0")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2026-06-03T21:00:00")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2026-06-03T21:00:00")
    private LocalDateTime updatedAt;
}
