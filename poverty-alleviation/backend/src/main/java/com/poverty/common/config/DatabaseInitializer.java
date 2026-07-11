package com.poverty.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据库初始化 — 启动时自动创建表并导入测试数据
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            java.sql.Connection conn = dataSource.getConnection();
            java.sql.Statement stmt = conn.createStatement();

            // 先清理旧数据（如果有的话）
            log.info("===== 正在重置数据库... =====");
            stmt.execute("DROP TABLE IF EXISTS `volunteer_record`");
            stmt.execute("DROP TABLE IF EXISTS `volunteer_activity`");
            stmt.execute("DROP TABLE IF EXISTS `training_enrollment`");
            stmt.execute("DROP TABLE IF EXISTS `training_course`");
            stmt.execute("DROP TABLE IF EXISTS `job_application`");
            stmt.execute("DROP TABLE IF EXISTS `job_position`");
            stmt.execute("DROP TABLE IF EXISTS `donation_goods`");
            stmt.execute("DROP TABLE IF EXISTS `donation_money`");
            stmt.execute("DROP TABLE IF EXISTS `needs_publish`");
            stmt.execute("DROP TABLE IF EXISTS `project_audit_log`");
            stmt.execute("DROP TABLE IF EXISTS `poverty_project`");
            stmt.execute("DROP TABLE IF EXISTS `visit_record`");
            stmt.execute("DROP TABLE IF EXISTS `assistance_measure`");
            stmt.execute("DROP TABLE IF EXISTS `assistance_pairing`");
            stmt.execute("DROP TABLE IF EXISTS `family_member`");
            stmt.execute("DROP TABLE IF EXISTS `poverty_family`");
            stmt.execute("DROP TABLE IF EXISTS `news_article`");
            stmt.execute("DROP TABLE IF EXISTS `sys_role_permission`");
            stmt.execute("DROP TABLE IF EXISTS `sys_user_role`");
            stmt.execute("DROP TABLE IF EXISTS `sys_permission`");
            stmt.execute("DROP TABLE IF EXISTS `sys_role`");
            stmt.execute("DROP TABLE IF EXISTS `sys_user`");
            stmt.close();
            log.info("旧表已清理");

            // 执行建表脚本
            ScriptUtils.executeSqlScript(conn, new org.springframework.core.io.ClassPathResource("init-schema.sql"));
            log.info("建表脚本执行完成");

            // 执行测试数据脚本
            ScriptUtils.executeSqlScript(conn, new org.springframework.core.io.ClassPathResource("test-data.sql"));
            log.info("测试数据导入完成");
            log.info("===== 数据库初始化完成 =====");
            log.info("管理员账号: admin / 123456");
            log.info("帮扶责任人: zhangjianjun / 123456");
            log.info("志愿  者:   zhaohongxia / 123456");

            conn.close();
        } catch (Exception e) {
            log.error("数据库初始化失败: {}", e.getMessage(), e);
        }
    }
}
