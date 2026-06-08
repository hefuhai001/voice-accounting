package com.hfh.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 提醒表实体
 */
@Data
@TableName("reminder")
@Schema(description = "提醒表实体")
public class ReminderEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID（雪花算法）", example = "1234567890123456789")
    private Long id;

    /** 用户ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 提醒标题 */
    @Schema(description = "提醒标题", example = "信用卡还款提醒")
    private String title;

    /** 提醒金额 */
    @Schema(description = "提醒金额", example = "5000.00")
    private BigDecimal amount;

    /** 首次提醒日期 */
    @Schema(description = "首次提醒日期", example = "2026-06-10")
    private LocalDate remindDate;

    /** 重复频率：1-一次性 2-每天 3-每周 4-每月 5-每年 */
    @Schema(description = "重复频率：1-一次性 2-每天 3-每周 4-每月 5-每年", example = "4")
    private Integer frequency;

    /** 是否已重复提醒过：0-否 1-是 */
    @Schema(description = "是否已重复提醒过：0-否 1-是", example = "0")
    private Integer isRepeated;

    /** 状态：0-待提醒 1-已提醒 2-已关闭 */
    @Schema(description = "状态：0-待提醒 1-已提醒 2-已关闭", example = "0")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注", example = "每月10号还款")
    private String remark;

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
