-- ============================================================
-- 扶贫帮扶管理系统 - 数据库初始化脚本
-- 数据库: poverty_db
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `poverty_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `poverty_db`;

-- ============================================================
-- 1. 系统权限相关表（5 张）
-- ============================================================

-- 1.1 系统用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(50)  NOT NULL COMMENT '登录名',
  `password`    VARCHAR(255) NOT NULL COMMENT '加密密码（BCrypt）',
  `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  `id_card`     VARCHAR(18)  DEFAULT NULL COMMENT '身份证号（唯一）',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `role_code`   VARCHAR(30)  NOT NULL COMMENT '角色标识（poor/cadre/volunteer/admin）',
  `status`      TINYINT      DEFAULT '1' COMMENT '状态：0 禁用，1 正常',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
  `deleted`     TINYINT      DEFAULT '0' COMMENT '逻辑删除：0 未删，1 已删',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 1.2 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
  `role_code`   VARCHAR(50)  NOT NULL COMMENT '角色唯一标识',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 1.3 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `perm_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `perm_name`  VARCHAR(50)  NOT NULL COMMENT '权限名称',
  `perm_code`  VARCHAR(100) NOT NULL COMMENT '权限标识（如 sys:user:add）',
  `parent_id`  BIGINT       DEFAULT NULL COMMENT '父级权限 ID',
  `type`       TINYINT      DEFAULT NULL COMMENT '类型：1 菜单，2 按钮，3 接口',
  PRIMARY KEY (`perm_id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 1.4 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 1.5 角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `perm_id` BIGINT NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================
-- 2. 业务数据表（17 张）
-- ============================================================

-- 2.1 贫困户家庭档案表
DROP TABLE IF EXISTS `poverty_family`;
CREATE TABLE `poverty_family` (
  `family_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `family_code`       VARCHAR(50)  DEFAULT NULL COMMENT '家庭档案编号',
  `householder_id`    BIGINT       DEFAULT NULL COMMENT '户主用户 ID（关联 sys_user）',
  `householder_name`  VARCHAR(50)  NOT NULL COMMENT '户主姓名',
  `id_card`           VARCHAR(18)  DEFAULT NULL COMMENT '户主身份证号',
  `province`          VARCHAR(50)  DEFAULT NULL COMMENT '省',
  `city`              VARCHAR(50)  DEFAULT NULL COMMENT '市',
  `district`          VARCHAR(50)  DEFAULT NULL COMMENT '区/县',
  `town`              VARCHAR(50)  DEFAULT NULL COMMENT '镇/乡',
  `village`           VARCHAR(50)  DEFAULT NULL COMMENT '村',
  `address`           VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
  `family_size`       INT          DEFAULT NULL COMMENT '家庭人口数',
  `annual_income`     DECIMAL(12,2) DEFAULT NULL COMMENT '年收入（元）',
  `poverty_cause_code` VARCHAR(50) DEFAULT NULL COMMENT '致贫原因编码',
  `poverty_level`     VARCHAR(20)  DEFAULT NULL COMMENT '贫困程度',
  `status`            VARCHAR(20)  DEFAULT '建档' COMMENT '状态：建档/已脱贫/已返贫',
  `filing_date`       DATE         DEFAULT NULL COMMENT '建档日期',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`family_id`),
  KEY `idx_householder_id` (`householder_id`),
  KEY `idx_id_card` (`id_card`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贫困户家庭档案表';

-- 2.2 家庭成员表
DROP TABLE IF EXISTS `family_member`;
CREATE TABLE `family_member` (
  `member_id`     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `family_id`     BIGINT      NOT NULL COMMENT '家庭 ID',
  `name`          VARCHAR(50) NOT NULL COMMENT '姓名',
  `relationship`  VARCHAR(20) DEFAULT NULL COMMENT '与户主关系',
  `id_card`       VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `phone`         VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `gender`        VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `age`           INT         DEFAULT NULL COMMENT '年龄',
  `health_status` VARCHAR(50) DEFAULT NULL COMMENT '健康状况',
  `has_job`       TINYINT     DEFAULT '0' COMMENT '是否有工作：0 无，1 有',
  `education`     VARCHAR(20) DEFAULT NULL COMMENT '文化程度',
  `remarks`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`member_id`),
  KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭成员表';

-- 2.3 帮扶结对表
DROP TABLE IF EXISTS `assistance_pairing`;
CREATE TABLE `assistance_pairing` (
  `pairing_id`   BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cadre_user_id` BIGINT NOT NULL COMMENT '帮扶干部用户 ID',
  `family_id`    BIGINT  NOT NULL COMMENT '家庭 ID',
  `pair_date`    DATE    DEFAULT NULL COMMENT '结对日期',
  `end_date`     DATE    DEFAULT NULL COMMENT '结束日期',
  `status`       VARCHAR(10) DEFAULT '1' COMMENT '状态：0 已结束，1 结对中',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`pairing_id`),
  KEY `idx_cadre_user` (`cadre_user_id`),
  KEY `idx_family` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帮扶结对表';

-- 2.4 帮扶措施表
DROP TABLE IF EXISTS `assistance_measure`;
CREATE TABLE `assistance_measure` (
  `measure_id`   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pairing_id`   BIGINT        DEFAULT NULL COMMENT '结对 ID',
  `family_id`    BIGINT        NOT NULL COMMENT '家庭 ID',
  `measure_type` VARCHAR(50)   DEFAULT NULL COMMENT '措施类型（产业/教育/医疗等）',
  `content`      TEXT          COMMENT '措施内容',
  `target_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '目标金额',
  `actual_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '实际金额',
  `progress`     INT           DEFAULT '0' COMMENT '进度百分比（0-100）',
  `status`       VARCHAR(10)   DEFAULT '0' COMMENT '状态：0 未启动/1 进行中/2 已完成/3 已取消',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`measure_id`),
  KEY `idx_family` (`family_id`),
  KEY `idx_pairing` (`pairing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帮扶措施表';

-- 2.5 走访记录表
DROP TABLE IF EXISTS `visit_record`;
CREATE TABLE `visit_record` (
  `record_id`    BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cadre_user_id` BIGINT NOT NULL COMMENT '走访干部用户 ID',
  `family_id`    BIGINT  NOT NULL COMMENT '走访家庭 ID',
  `visit_date`   DATE    DEFAULT NULL COMMENT '走访日期',
  `content`      TEXT    COMMENT '走访内容',
  `photos`       VARCHAR(2000) DEFAULT NULL COMMENT '照片 URL（JSON 数组）',
  `next_plan`    TEXT    COMMENT '下一步帮扶计划',
  `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_cadre_user` (`cadre_user_id`),
  KEY `idx_family` (`family_id`),
  KEY `idx_visit_date` (`visit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='走访记录表';

-- 2.6 扶贫项目表
DROP TABLE IF EXISTS `poverty_project`;
CREATE TABLE `poverty_project` (
  `project_id`   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_name` VARCHAR(200)  NOT NULL COMMENT '项目名称',
  `family_id`    BIGINT        DEFAULT NULL COMMENT '关联家庭 ID（可空）',
  `proposer_id`  BIGINT        NOT NULL COMMENT '申请人 ID（帮扶干部）',
  `budget`       DECIMAL(12,2) DEFAULT NULL COMMENT '项目预算',
  `raised_amount` DECIMAL(12,2) DEFAULT '0.00' COMMENT '已筹集金额',
  `description`  TEXT          COMMENT '项目描述',
  `status`       VARCHAR(20)   DEFAULT '0' COMMENT '状态：0 待审核/1 已通过/2 已驳回/3 进行中/4 已完成',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`project_id`),
  KEY `idx_family` (`family_id`),
  KEY `idx_proposer` (`proposer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扶贫项目表';

-- 2.7 项目审计日志表
DROP TABLE IF EXISTS `project_audit_log`;
CREATE TABLE `project_audit_log` (
  `log_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` BIGINT       NOT NULL COMMENT '项目 ID',
  `auditor_id` BIGINT       NOT NULL COMMENT '审核人 ID',
  `action`     VARCHAR(50)  NOT NULL COMMENT '操作（审核通过/驳回/提交等）',
  `comment`    VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目审计日志表';

-- 2.8 困难需求发布表
DROP TABLE IF EXISTS `needs_publish`;
CREATE TABLE `needs_publish` (
  `need_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `family_id`     BIGINT        NOT NULL COMMENT '家庭 ID',
  `publisher_id`  BIGINT        NOT NULL COMMENT '发布人 ID',
  `need_type`     VARCHAR(20)   DEFAULT NULL COMMENT '需求类型（资金/物资/技术）',
  `title`         VARCHAR(200)  NOT NULL COMMENT '需求标题',
  `description`   TEXT          COMMENT '需求描述',
  `target_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '目标金额/价值',
  `actual_amount` DECIMAL(12,2) DEFAULT '0.00' COMMENT '已获金额/价值',
  `status`        VARCHAR(10)   DEFAULT '0' COMMENT '状态：0 待解决/1 已对接/2 已完成',
  `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`need_id`),
  KEY `idx_family` (`family_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='困难需求发布表';

-- 2.9 资金捐赠记录表
DROP TABLE IF EXISTS `donation_money`;
CREATE TABLE `donation_money` (
  `money_donation_id` BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `donor_name`        VARCHAR(50)   NOT NULL COMMENT '捐赠人/单位',
  `amount`            DECIMAL(12,2) NOT NULL COMMENT '捐赠金额',
  `payment_method`    VARCHAR(50)   DEFAULT NULL COMMENT '支付方式',
  `transaction_id`    VARCHAR(100)  DEFAULT NULL COMMENT '支付流水号',
  `recorder_id`       BIGINT        NOT NULL COMMENT '登记人 ID',
  `status`            TINYINT       DEFAULT '0' COMMENT '1 已到账，0 待确认',
  `need_id`           BIGINT        DEFAULT NULL COMMENT '关联需求 ID（可空）',
  `project_id`        BIGINT        DEFAULT NULL COMMENT '关联项目 ID（可空）',
  `donate_time`       DATETIME      DEFAULT NULL COMMENT '捐赠时间',
  `is_anonymous`      TINYINT       DEFAULT '0' COMMENT '是否匿名：1 是，0 否',
  PRIMARY KEY (`money_donation_id`),
  KEY `idx_need` (`need_id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_recorder` (`recorder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金捐赠记录表';

-- 2.10 物资捐赠记录表
DROP TABLE IF EXISTS `donation_goods`;
CREATE TABLE `donation_goods` (
  `goods_donation_id` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `donor_name`        VARCHAR(50)  NOT NULL COMMENT '捐赠人/单位',
  `goods_name`        VARCHAR(100) NOT NULL COMMENT '物资名称',
  `quantity`          INT          NOT NULL COMMENT '数量',
  `unit`              VARCHAR(20)  DEFAULT NULL COMMENT '单位',
  `need_id`           BIGINT       DEFAULT NULL COMMENT '关联需求 ID',
  `logistics_info`    VARCHAR(200) DEFAULT NULL COMMENT '物流/发放信息',
  `recorder_id`       BIGINT       NOT NULL COMMENT '登记人 ID',
  `receive_family_id` BIGINT       DEFAULT NULL COMMENT '接收贫困户 ID（可空）',
  `status`            TINYINT      DEFAULT '1' COMMENT '1 已接收，2 已发放，3 已反馈',
  `donate_time`       DATETIME     DEFAULT NULL COMMENT '捐赠时间',
  PRIMARY KEY (`goods_donation_id`),
  KEY `idx_need` (`need_id`),
  KEY `idx_recorder` (`recorder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资捐赠记录表';

-- 2.11 就业岗位表
DROP TABLE IF EXISTS `job_position`;
CREATE TABLE `job_position` (
  `job_id`       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `publisher_id` BIGINT       NOT NULL COMMENT '发布人 ID',
  `title`        VARCHAR(100) NOT NULL COMMENT '岗位名称',
  `company`      VARCHAR(100) DEFAULT NULL COMMENT '招聘单位',
  `requirements` TEXT         COMMENT '岗位要求',
  `salary_range` VARCHAR(50)  DEFAULT NULL COMMENT '薪资范围',
  `workplace`    VARCHAR(200) DEFAULT NULL COMMENT '工作地点',
  `contact`      VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `is_valid`     TINYINT      DEFAULT '1' COMMENT '1 有效，0 下架',
  `publish_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`job_id`),
  KEY `idx_publisher` (`publisher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就业岗位表';

-- 2.12 岗位申请表
DROP TABLE IF EXISTS `job_application`;
CREATE TABLE `job_application` (
  `apply_id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_id`           BIGINT       NOT NULL COMMENT '岗位 ID',
  `applicant_user_id` BIGINT      NOT NULL COMMENT '申请人用户 ID',
  `apply_status`     TINYINT      DEFAULT '1' COMMENT '1 申请中，2 通过，3 拒绝',
  `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `apply_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`apply_id`),
  KEY `idx_job` (`job_id`),
  KEY `idx_applicant` (`applicant_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位申请表';

-- 2.13 技能培训课程表
DROP TABLE IF EXISTS `training_course`;
CREATE TABLE `training_course` (
  `course_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`         VARCHAR(200) NOT NULL COMMENT '课程标题',
  `content`       TEXT         COMMENT '课程内容',
  `trainer`       VARCHAR(50)  DEFAULT NULL COMMENT '培训讲师',
  `start_time`    DATETIME     DEFAULT NULL COMMENT '开始时间',
  `end_time`      DATETIME     DEFAULT NULL COMMENT '结束时间',
  `location`      VARCHAR(200) DEFAULT NULL COMMENT '培训地点（可填线上链接）',
  `max_enroll`    INT          DEFAULT '0' COMMENT '最大报名人数',
  `enrolled_count` INT         DEFAULT '0' COMMENT '已报名人数',
  `status`        TINYINT      DEFAULT '1' COMMENT '1 预告，2 报名中，3 进行中，4 已结束',
  `publisher_id`  BIGINT       NOT NULL COMMENT '发布人 ID',
  PRIMARY KEY (`course_id`),
  KEY `idx_publisher` (`publisher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能培训课程表';

-- 2.14 培训报名表
DROP TABLE IF EXISTS `training_enrollment`;
CREATE TABLE `training_enrollment` (
  `enroll_id`    BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id`    BIGINT   NOT NULL COMMENT '课程 ID',
  `user_id`      BIGINT   NOT NULL COMMENT '报名用户 ID',
  `sign_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
  `status`       TINYINT  DEFAULT '1' COMMENT '1 已报名，2 已签到，3 缺勤',
  `enroll_time`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`enroll_id`),
  KEY `idx_course` (`course_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训报名表';

-- 2.15 志愿者活动表（增强版）
DROP TABLE IF EXISTS `volunteer_activity`;
CREATE TABLE `volunteer_activity` (
  `activity_id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`            VARCHAR(200) NOT NULL COMMENT '活动标题',
  `description`      TEXT         COMMENT '活动描述',
  `activity_type`    VARCHAR(30)  DEFAULT '综合' COMMENT '活动分类：走访慰问/技能培训/物资分发/环境整治/医疗义诊/教育辅导',
  `start_time`       DATETIME     DEFAULT NULL COMMENT '开始时间',
  `end_time`         DATETIME     DEFAULT NULL COMMENT '结束时间',
  `location`         VARCHAR(200) DEFAULT NULL COMMENT '活动地点',
  `cover_image`      VARCHAR(500) DEFAULT NULL COMMENT '活动封面图 URL',
  `contact_phone`    VARCHAR(20)  DEFAULT NULL COMMENT '活动联系人电话',
  `difficulty`       TINYINT      DEFAULT 1 COMMENT '活动难度等级 1-5',
  `family_id`        BIGINT       DEFAULT NULL COMMENT '关联帮扶家庭 ID（可选）',
  `organizer_id`     BIGINT       NOT NULL COMMENT '组织者用户 ID',
  `need_volunteers`  INT          DEFAULT '0' COMMENT '需要人数',
  `registered_count` INT          DEFAULT '0' COMMENT '已报名人数',
  `status`           TINYINT      DEFAULT '1' COMMENT '状态：0 草稿/1 招募中/2 进行中/3 已结束/4 已取消',
  `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`activity_id`),
  KEY `idx_organizer` (`organizer_id`),
  KEY `idx_family` (`family_id`),
  KEY `idx_type` (`activity_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿者活动表';

-- 2.16 志愿者服务记录表（增强版）
DROP TABLE IF EXISTS `volunteer_record`;
CREATE TABLE `volunteer_record` (
  `record_id`         BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id`       BIGINT         NOT NULL COMMENT '活动 ID',
  `volunteer_user_id` BIGINT         NOT NULL COMMENT '志愿者用户 ID',
  `family_id`         BIGINT         DEFAULT NULL COMMENT '实际服务的帮扶家庭 ID',
  `sign_in_time`      DATETIME       DEFAULT NULL COMMENT '签到时间',
  `sign_out_time`     DATETIME       DEFAULT NULL COMMENT '签退时间',
  `service_hours`     DECIMAL(5,2)   DEFAULT '0.00' COMMENT '服务时长（小时）',
  `service_content`   TEXT           COMMENT '服务内容描述',
  `photos`            VARCHAR(2000)  DEFAULT NULL COMMENT '服务照片 URL（JSON 数组）',
  `beneficiary_feedback` TEXT        DEFAULT NULL COMMENT '受益对象反馈',
  `beneficiary_rating` TINYINT       DEFAULT NULL COMMENT '受益对象打分 1-5',
  `status`            TINYINT        DEFAULT '1' COMMENT '状态：1 已报名/2 已签到/3 已完成/4 已评价',
  PRIMARY KEY (`record_id`),
  KEY `idx_activity` (`activity_id`),
  KEY `idx_volunteer` (`volunteer_user_id`),
  KEY `idx_family` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿者服务记录表';

-- 2.17 文章/公告表（信息公开）
DROP TABLE IF EXISTS `news_article`;
CREATE TABLE `news_article` (
  `article_id`  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`       VARCHAR(200) NOT NULL COMMENT '标题',
  `content`     TEXT         COMMENT '内容',
  `type`        VARCHAR(50)  DEFAULT NULL COMMENT '类型：政策新闻/脱贫案例/通知公告/捐赠公示',
  `publisher_id` BIGINT      NOT NULL COMMENT '发布人 ID',
  `view_count`  INT          DEFAULT '0' COMMENT '浏览次数',
  `is_top`      TINYINT      DEFAULT '0' COMMENT '是否置顶：0 否，1 是',
  `status`      TINYINT      DEFAULT '0' COMMENT '0 草稿，1 已发布',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`article_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章/公告表';

-- 2.18 志愿者积分表
DROP TABLE IF EXISTS `volunteer_score`;
CREATE TABLE `volunteer_score` (
  `score_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '志愿者用户 ID',
  `score_type`  VARCHAR(30)  NOT NULL COMMENT '积分类型：service_hour/difficulty_bonus/training/referral/rating_bonus',
  `score`       INT          NOT NULL DEFAULT 0 COMMENT '获得积分',
  `source_id`   BIGINT       DEFAULT NULL COMMENT '来源记录 ID',
  `source_type` VARCHAR(30)  DEFAULT NULL COMMENT '来源类型：activity/training/need',
  `remark`      VARCHAR(200) DEFAULT NULL COMMENT '积分说明',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`score_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`score_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿者积分表';

-- 2.19 志愿者等级表
DROP TABLE IF EXISTS `volunteer_level`;
CREATE TABLE `volunteer_level` (
  `level_id`   INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level_name` VARCHAR(50)  NOT NULL COMMENT '等级名称',
  `min_score`  INT          NOT NULL DEFAULT 0 COMMENT '所需最低积分',
  `badge_icon` VARCHAR(200) DEFAULT NULL COMMENT '徽章图标 URL',
  PRIMARY KEY (`level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿者等级表';

-- 2.20 志愿者证书表
DROP TABLE IF EXISTS `volunteer_certificate`;
CREATE TABLE `volunteer_certificate` (
  `cert_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '志愿者用户 ID',
  `cert_type`   VARCHAR(30)  NOT NULL COMMENT '证书类型：service_hours/training/activity',
  `source_id`   BIGINT       DEFAULT NULL COMMENT '来源 ID',
  `cert_name`   VARCHAR(200) DEFAULT NULL COMMENT '证书名称',
  `cert_number` VARCHAR(50)  NOT NULL COMMENT '证书编号',
  `issue_date`  DATE         NOT NULL COMMENT '颁发日期',
  `file_url`    VARCHAR(500) DEFAULT NULL COMMENT '证书文件路径',
  PRIMARY KEY (`cert_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`cert_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿者证书表';

-- ============================================================
-- 3. 跨模块增强 — 已有表新增关联字段
-- ============================================================

-- 3.1 困难需求表：增加志愿者认领字段
ALTER TABLE `needs_publish`
  ADD COLUMN `volunteer_id` BIGINT DEFAULT NULL COMMENT '认领志愿者用户 ID' AFTER `actual_amount`,
  ADD COLUMN `volunteer_confirm_time` DATETIME DEFAULT NULL COMMENT '认领确认时间' AFTER `volunteer_id`,
  ADD INDEX `idx_volunteer_needs` (`volunteer_id`);

-- 3.2 物资捐赠表：增加分发人字段
ALTER TABLE `donation_goods`
  ADD COLUMN `distributed_by` BIGINT DEFAULT NULL COMMENT '分发志愿者用户 ID' AFTER `receive_family_id`,
  ADD INDEX `idx_distributor` (`distributed_by`);


-- ============================================================
-- 4. 初始数据
-- ============================================================

-- 4.1 初始角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_code`, `description`) VALUES
(1, '系统管理员',   'admin',     '系统管理员，拥有全部权限'),
(2, '帮扶责任人',   'cadre',     '扶贫干部，管理结对贫困户'),
(3, '贫困户/帮扶对象', 'poor',   '贫困户，查看档案与申请帮扶'),
(4, '志愿者/社工',   'volunteer', '志愿者与社会工作者');

-- 4.2 初始权限
INSERT INTO `sys_permission` (`perm_id`, `perm_name`, `perm_code`, `parent_id`, `type`) VALUES
(1,  '全部权限',     'admin:all',          NULL, 1),
(2,  '系统管理',     'system:manage',      NULL, 1),
(3,  '用户管理',     'system:user:manage', 2,    1),
(4,  '新增用户',     'system:user:add',    3,    2),
(5,  '编辑用户',     'system:user:edit',   3,    2),
(6,  '删除用户',     'system:user:delete', 3,    2),
(7,  '角色管理',     'system:role:manage', 2,    1),
(8,  '权限管理',     'system:perm:manage', 2,    1),
(9,  '贫困户管理',   'poverty:manage',     NULL, 1),
(10, '档案管理',     'poverty:family:crud',9,    1),
(11, '走访记录',     'visit:manage',       NULL, 1),
(12, '项目管理',     'project:manage',     NULL, 1),
(13, '捐赠管理',     'donation:manage',    NULL, 1),
(14, '就业管理',     'job:manage',         NULL, 1),
(15, '培训管理',     'training:manage',    NULL, 1),
(16, '志愿活动',     'volunteer:manage',   NULL, 1),
(17, '积分管理',     'volunteer:score',    16,   2),
(18, '证书管理',     'volunteer:cert',     16,   2),
(19, '积分排行',     'volunteer:ranking',  16,   2);

-- 4.3 初始志愿者等级
INSERT INTO `volunteer_level` (`level_id`, `level_name`, `min_score`, `badge_icon`) VALUES
(1, '一星志愿者', 0,    '/badges/star1.svg'),
(2, '二星志愿者', 100,  '/badges/star2.svg'),
(3, '三星志愿者', 300,  '/badges/star3.svg'),
(4, '四星志愿者', 600,  '/badges/star4.svg'),
(5, '五星志愿者', 1000, '/badges/star5.svg');

-- 4.4 初始管理员（密码：123456）
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `real_name`, `role_code`, `status`) VALUES
(1, 'admin', '$2a$10$X3T.WITx6jz23eAY5FESJ.W6CAOZ9YEDCjxxEJZOWNn0v4vkylsIi', '系统管理员', 'admin', 1);

-- 4.5 初始用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 4.6 初始角色权限关联（admin 角色拥有所有权限）
INSERT INTO `sys_role_permission` (`role_id`, `perm_id`)
SELECT 1, `perm_id` FROM `sys_permission`;

