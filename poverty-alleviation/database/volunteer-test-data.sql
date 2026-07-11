-- ============================================================
-- 志愿者模块测试数据补充脚本
-- 在 test-data.sql 执行之后运行
-- 补齐新字段的数据 + 积分/等级/证书数据
-- ============================================================

USE `poverty_db`;

-- ============================================================
-- 1. 更新志愿活动：补齐新字段
-- ============================================================
UPDATE `volunteer_activity` SET
  `activity_type` = '走访慰问',
  `difficulty` = 2,
  `status` = 3
WHERE `activity_id` = 1;

UPDATE `volunteer_activity` SET
  `activity_type` = '环境整治',
  `difficulty` = 3,
  `status` = 3
WHERE `activity_id` = 2;

UPDATE `volunteer_activity` SET
  `activity_type` = '教育辅导',
  `difficulty` = 1,
  `status` = 1
WHERE `activity_id` = 3;

-- ============================================================
-- 2. 新增一个进行中的活动（可用于测试报名/签到/签退）
-- ============================================================
INSERT INTO `volunteer_activity` (`title`, `description`, `activity_type`, `start_time`, `end_time`, `location`, `organizer_id`, `need_volunteers`, `registered_count`, `difficulty`, `family_id`, `status`)
VALUES ('走访慰问王大山家庭', '前往田湖镇小安头村看望王大山一家，了解近期生活困难', '走访慰问', NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR), '田湖镇小安头村3组', 7, 5, 0, 2, 1, 1);

-- ============================================================
-- 3. 志愿者积分数据
-- ============================================================
INSERT INTO `volunteer_score` (`user_id`, `score_type`, `score`, `source_id`, `source_type`, `remark`, `create_time`) VALUES
(7, 'service_hour',  90,  1, 'activity', '春节送温暖慰问活动 9小时',       '2024-01-25 18:00:00'),
(7, 'service_hour',  45,  3, 'activity', '环境卫生整治 4.5小时',             '2024-04-20 13:00:00'),
(7, 'difficulty_bonus', 30, 3, 'activity', '环境整治难度奖励(3级)',          '2024-04-20 13:00:00'),
(7, 'training',      20,  3, 'training', '完成电子商务入门课程培训',         '2024-06-05 17:00:00'),
(7, 'rating_bonus',  20,  1, 'activity', '受益方评价5星',                   '2024-01-26 10:00:00'),
(8, 'service_hour',  70,  2, 'activity', '春节送温暖慰问活动 7小时',         '2024-01-25 17:00:00'),
(8, 'service_hour',  40,  4, 'activity', '环境卫生整治 4小时',               '2024-04-20 13:00:00'),
(8, 'difficulty_bonus', 20, 4, 'activity', '环境整治难度奖励(3级)',          '2024-04-20 13:00:00');

-- ============================================================
-- 4. 志愿者证书数据
-- ============================================================
INSERT INTO `volunteer_certificate` (`user_id`, `cert_type`, `source_id`, `cert_name`, `cert_number`, `issue_date`) VALUES
(7, 'service_hours', NULL, '志愿服务证明（累计20小时）', 'VOL-2024-7A3B2C1D', '2024-04-20'),
(8, 'service_hours', NULL, '志愿服务证明（累计10小时）', 'VOL-2024-8E5F6G7H', '2024-04-20');

-- ============================================================
-- 完成
-- ============================================================
SELECT '志愿者测试数据已补充' AS result;
