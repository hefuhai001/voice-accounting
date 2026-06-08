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
 * 记账记录表实体
 */
@Data
@TableName("transaction")
@Schema(description = "记账记录表实体")
public class TransactionEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID（雪花算法）", example = "1234567890123456789")
    private Long id;

    /** 账本ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "账本ID", example = "1")
    private Long bookId;

    /** 分类ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /** 金额（正数，由 type 区分收支） */
    @Schema(description = "金额（正数，由type区分收支）", example = "100.00")
    private BigDecimal amount;

    /** 类型：1-支出 2-收入 */
    @Schema(description = "类型：1-支出 2-收入", example = "1")
    private Integer type;

    /** 备注 */
    @Schema(description = "备注", example = "午餐费用")
    private String remark;

    /** 语音识别原文 */
    @Schema(description = "语音识别原文", example = "今天午餐花了五十块")
    private String voiceText;

    /** 交易日期 */
    @Schema(description = "交易日期", example = "2026-06-03")
    private LocalDate transactionDate;

    /** 凭证图片地址 */
    @Schema(description = "凭证图片地址", example = "https://example.com/receipt.jpg")
    private String imageUrl;

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
