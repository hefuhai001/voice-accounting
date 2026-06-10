-- =============================================
-- 语音记账应用数据库表结构
-- Database: db_voice_accounting
-- =============================================

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(200) NOT NULL COMMENT '密码（加密存储）',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(500) DEFAULT NULL COMMENT '头像地址',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `role`        TINYINT      DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 2. 账本表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `account_book` (
    `id`          BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`     BIGINT        NOT NULL COMMENT '用户ID',
    `name`        VARCHAR(50)   NOT NULL COMMENT '账本名称',
    `type`        TINYINT       DEFAULT 1 COMMENT '账本类型：1-日常账本 2-旅行账本 3-共享账本',
    `description` VARCHAR(255)  DEFAULT NULL COMMENT '账本描述',
    `is_default`  TINYINT       DEFAULT 0 COMMENT '是否默认账本：0-否 1-是',
    `icon`        VARCHAR(100)  DEFAULT NULL COMMENT '账本图标',
    `sort_order`  INT           DEFAULT 0 COMMENT '排序序号',
    `deleted`     TINYINT       DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账本表';

-- ----------------------------
-- 3. 分类表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `category` (
    `id`          BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `name`        VARCHAR(30)  NOT NULL COMMENT '分类名称',
    `icon`        VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
    `type`        TINYINT      NOT NULL COMMENT '分类类型：1-支出 2-收入',
    `user_id`     BIGINT       DEFAULT NULL COMMENT '用户ID（NULL表示系统默认分类）',
    `sort_order`  INT          DEFAULT 0 COMMENT '排序序号',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ----------------------------
-- 4. 记账记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `transaction` (
    `id`              BIGINT         NOT NULL COMMENT '主键ID（雪花算法）',
    `book_id`         BIGINT         NOT NULL COMMENT '账本ID',
    `category_id`     BIGINT         NOT NULL COMMENT '分类ID',
    `amount`          DECIMAL(10, 2) NOT NULL COMMENT '金额（正数，由 type 区分收支）',
    `type`            TINYINT        NOT NULL COMMENT '类型：1-支出 2-收入',
    `remark`          VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    `voice_text`      TEXT           DEFAULT NULL COMMENT '语音识别原文',
    `transaction_date` DATE          NOT NULL COMMENT '交易日期',
    `image_url`       VARCHAR(500)   DEFAULT NULL COMMENT '凭证图片地址',
    `deleted`         TINYINT        DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_at`      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_transaction_date` (`transaction_date`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账记录表';

-- ----------------------------
-- 5. 提醒表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `reminder` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
    `title`         VARCHAR(100) NOT NULL COMMENT '提醒标题',
    `amount`        DECIMAL(10, 2) DEFAULT NULL COMMENT '提醒金额',
    `remind_date`   DATE         NOT NULL COMMENT '首次提醒日期',
    `frequency`     TINYINT      DEFAULT 1 COMMENT '重复频率：1-一次性 2-每天 3-每周 4-每月 5-每年',
    `is_repeated`   TINYINT      DEFAULT 0 COMMENT '是否已重复提醒过：0-否 1-是',
    `status`        TINYINT      DEFAULT 0 COMMENT '状态：0-待提醒 1-已提醒 2-已关闭',
    `remark`        VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_remind_date` (`remind_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提醒表';

-- ----------------------------
-- 初始化默认分类数据
-- ----------------------------
INSERT INTO `category` (`id`, `name`, `icon`, `type`, `user_id`, `sort_order`) VALUES
-- 支出分类 (type=1)
(1,  '餐饮',   '🍔',     1, NULL, 1),
(2,  '交通',   '🚗',      1, NULL, 2),
(3,  '购物',   '🛒', 1, NULL, 3),
(4,  '娱乐',   '🎮',     1, NULL, 4),
(5,  '居住',   '🏠',   1, NULL, 5),
(6,  '医疗',   '💊',  1, NULL, 6),
(7,  '教育',   '📚',1, NULL, 7),
(8,  '通讯',   '📱',    1, NULL, 8),
(9,  '其他',   '📌',     1, NULL, 99),
-- 收入分类 (type=2)
(10, '工资',   '💰',   2, NULL, 1),
(11, '奖金',   '🎁',    2, NULL, 2),
(12, '理财',   '📈',  2, NULL, 3),
(13, '兼职',   '💼', 2, NULL, 4),
(14, '其他',   '📌',     2, NULL, 99);
