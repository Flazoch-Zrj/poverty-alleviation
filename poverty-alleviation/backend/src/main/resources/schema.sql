-- ============================================================
-- 应用启动时自动修复 volunteer 表结构
-- MySQL 5.6.17 Bug: InnoDB 表定义重启后可能丢失新增列
-- 每次启动执行，如果列已存在则跳过（通过 continue-on-error）
-- ============================================================

-- 修复 volunteer_activity 表
ALTER TABLE `volunteer_activity` ADD COLUMN `activity_type` VARCHAR(30) DEFAULT '综合';
ALTER TABLE `volunteer_activity` ADD COLUMN `cover_image` VARCHAR(500);
ALTER TABLE `volunteer_activity` ADD COLUMN `contact_phone` VARCHAR(20);
ALTER TABLE `volunteer_activity` ADD COLUMN `difficulty` TINYINT DEFAULT 1;
ALTER TABLE `volunteer_activity` ADD COLUMN `family_id` BIGINT;
ALTER TABLE `volunteer_activity` ADD COLUMN `status` TINYINT DEFAULT 1;
ALTER TABLE `volunteer_activity` ADD INDEX `idx_family` (`family_id`);
ALTER TABLE `volunteer_activity` ADD INDEX `idx_type` (`activity_type`);
ALTER TABLE `volunteer_activity` ADD INDEX `idx_status` (`status`);

-- 修复 volunteer_record 表
ALTER TABLE `volunteer_record` ADD COLUMN `family_id` BIGINT;
ALTER TABLE `volunteer_record` ADD COLUMN `service_content` TEXT;
ALTER TABLE `volunteer_record` ADD COLUMN `photos` VARCHAR(2000);
ALTER TABLE `volunteer_record` ADD COLUMN `beneficiary_feedback` TEXT;
ALTER TABLE `volunteer_record` ADD COLUMN `beneficiary_rating` TINYINT;
ALTER TABLE `volunteer_record` ADD INDEX `idx_family` (`family_id`);
