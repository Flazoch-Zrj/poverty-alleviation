-- ============================================================
-- 志愿服务模块增强 — 数据库迁移脚本（MySQL 5.6 兼容版）
-- 数据库: poverty_db
-- 说明: 不依赖 CURRENT_TIMESTAMP 默认值，每条 ALTER 独立
-- ============================================================

USE `poverty_db`;

-- ============================================================
-- 1. volunteer_record 表：逐个添加字段
-- ============================================================
ALTER TABLE `volunteer_record`
  ADD COLUMN `family_id` BIGINT DEFAULT NULL COMMENT '实际服务的帮扶家庭 ID' AFTER `volunteer_user_id`;
ALTER TABLE `volunteer_record`
  ADD COLUMN `service_content` TEXT COMMENT '服务内容描述' AFTER `service_hours`;
ALTER TABLE `volunteer_record`
  ADD COLUMN `photos` VARCHAR(2000) DEFAULT NULL COMMENT '服务照片 URL（JSON 数组）' AFTER `service_content`;
ALTER TABLE `volunteer_record`
  ADD COLUMN `beneficiary_feedback` TEXT DEFAULT NULL COMMENT '受益对象反馈' AFTER `photos`;
ALTER TABLE `volunteer_record`
  ADD COLUMN `beneficiary_rating` TINYINT DEFAULT NULL COMMENT '受益对象打分 1-5' AFTER `beneficiary_feedback`;
ALTER TABLE `volunteer_record`
  ADD INDEX `idx_family` (`family_id`);

-- ============================================================
-- 2. volunteer_activity 表：逐个添加字段
-- ============================================================
ALTER TABLE `volunteer_activity`
  ADD COLUMN `activity_type` VARCHAR(30) DEFAULT '综合' COMMENT '活动分类' AFTER `description`;
ALTER TABLE `volunteer_activity`
  ADD COLUMN `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '活动封面图 URL' AFTER `location`;
ALTER TABLE `volunteer_activity`
  ADD COLUMN `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '活动联系人电话' AFTER `cover_image`;
ALTER TABLE `volunteer_activity`
  ADD COLUMN `difficulty` TINYINT DEFAULT 1 COMMENT '活动难度等级 1-5' AFTER `contact_phone`;
ALTER TABLE `volunteer_activity`
  ADD COLUMN `family_id` BIGINT DEFAULT NULL COMMENT '关联帮扶家庭 ID（可选）' AFTER `difficulty`;
ALTER TABLE `volunteer_activity`
  ADD INDEX `idx_family` (`family_id`);
ALTER TABLE `volunteer_activity`
  ADD INDEX `idx_type` (`activity_type`);
ALTER TABLE `volunteer_activity`
  ADD INDEX `idx_status` (`status`);

-- ============================================================
-- 3. 新建 volunteer_score（积分表）
-- ============================================================
DROP TABLE IF EXISTS `volunteer_score`;
CREATE TABLE `volunteer_score` (
  `score_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '志愿者用户 ID',
  `score_type`  VARCHAR(30)  NOT NULL COMMENT '积分类型',
  `score`       INT          NOT NULL DEFAULT 0 COMMENT '获得积分',
  `source_id`   BIGINT       DEFAULT NULL COMMENT '来源记录 ID',
  `source_type` VARCHAR(30)  DEFAULT NULL COMMENT '来源类型',
  `remark`      VARCHAR(200) DEFAULT NULL COMMENT '积分说明',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间（由 Java 自动填充）',
  PRIMARY KEY (`score_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`score_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='志愿者积分表';

-- ============================================================
-- 4. 新建 volunteer_level（等级表 + 初始数据）
-- ============================================================
DROP TABLE IF EXISTS `volunteer_level`;
CREATE TABLE `volunteer_level` (
  `level_id`   INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level_name` VARCHAR(50)  NOT NULL COMMENT '等级名称',
  `min_score`  INT          NOT NULL DEFAULT 0 COMMENT '所需最低积分',
  `badge_icon` VARCHAR(200) DEFAULT NULL COMMENT '徽章图标 URL',
  PRIMARY KEY (`level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='志愿者等级表';

INSERT INTO `volunteer_level` (`level_id`, `level_name`, `min_score`, `badge_icon`) VALUES
(1, '一星志愿者', 0,    '/badges/star1.svg'),
(2, '二星志愿者', 100,  '/badges/star2.svg'),
(3, '三星志愿者', 300,  '/badges/star3.svg'),
(4, '四星志愿者', 600,  '/badges/star4.svg'),
(5, '五星志愿者', 1000, '/badges/star5.svg');

-- ============================================================
-- 5. 新建 volunteer_certificate（证书表）
-- ============================================================
DROP TABLE IF EXISTS `volunteer_certificate`;
CREATE TABLE `volunteer_certificate` (
  `cert_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '志愿者用户 ID',
  `cert_type`   VARCHAR(30)  NOT NULL COMMENT '证书类型',
  `source_id`   BIGINT       DEFAULT NULL COMMENT '来源 ID',
  `cert_name`   VARCHAR(200) DEFAULT NULL COMMENT '证书名称',
  `cert_number` VARCHAR(50)  NOT NULL COMMENT '证书编号',
  `issue_date`  DATE         NOT NULL COMMENT '颁发日期',
  `file_url`    VARCHAR(500) DEFAULT NULL COMMENT '证书文件路径',
  PRIMARY KEY (`cert_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`cert_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='志愿者证书表';

-- ============================================================
-- 6. 跨模块关联字段
-- ============================================================
ALTER TABLE `needs_publish`
  ADD COLUMN `volunteer_id` BIGINT DEFAULT NULL COMMENT '认领志愿者用户 ID' AFTER `actual_amount`;
ALTER TABLE `needs_publish`
  ADD COLUMN `volunteer_confirm_time` DATETIME DEFAULT NULL COMMENT '认领确认时间' AFTER `volunteer_id`;
ALTER TABLE `needs_publish`
  ADD INDEX `idx_volunteer_needs` (`volunteer_id`);

ALTER TABLE `donation_goods`
  ADD COLUMN `distributed_by` BIGINT DEFAULT NULL COMMENT '分发志愿者用户 ID' AFTER `receive_family_id`;
ALTER TABLE `donation_goods`
  ADD INDEX `idx_distributor` (`distributed_by`);

-- ============================================================
-- 完成
-- ============================================================
SELECT '志愿者模块升级完成' AS result;
