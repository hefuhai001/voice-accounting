package com.hfh.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类表实体
 */
@Data
@TableName("category")
@Schema(description = "分类表实体")
public class CategoryEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID（雪花算法）", example = "1234567890123456789")
    private Long id;

    /** 分类名称 */
    @Schema(description = "分类名称", example = "餐饮")
    private String name;

    /** 分类图标 */
    @Schema(description = "分类图标", example = "food.png")
    private String icon;

    /** 分类类型：1-支出 2-收入 */
    @Schema(description = "分类类型：1-支出 2-收入", example = "1")
    private Integer type;

    /** 用户ID（NULL表示系统默认分类） */
    @Schema(description = "用户ID（NULL表示系统默认分类）", example = "1")
    private Long userId;

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
