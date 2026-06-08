package com.hfh.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 账本表实体
 */
@Data
@TableName("account_book")
@Schema(description = "账本表实体")
public class AccountBookEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID（雪花算法）", example = "1234567890123456789")
    private Long id;

    /** 用户ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 账本名称 */
    @Schema(description = "账本名称", example = "日常账本")
    private String name;

    /** 账本类型：1-日常账本 2-旅行账本 3-共享账本 */
    @Schema(description = "账本类型：1-日常账本 2-旅行账本 3-共享账本", example = "1")
    private Integer type;

    /** 账本描述 */
    @Schema(description = "账本描述", example = "用于记录日常开销")
    private String description;

    /** 是否默认账本：0-否 1-是 */
    @Schema(description = "是否默认账本：0-否 1-是", example = "1")
    private Integer isDefault;

    /** 账本图标 */
    @Schema(description = "账本图标", example = "book.png")
    private String icon;

    /** 排序序号 */
    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;

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
