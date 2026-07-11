package com.poverty.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Fix volunteer table structure on startup for MySQL 5.6.17 InnoDB bug.
 */
@Slf4j
@Component
public class VolunteerTableFixRunner implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== Checking volunteer table structure ===");
        fixVolunteerActivity();
        fixVolunteerRecord();
        fixJobPosition();
        fixTrainingCourse();
        fixNeedsPublish();
        fixDonationGoods();
        fixPovertyFamily();
        fixVisitRecord();
        log.info("=== Table structure check done ===");
    }

    private void fixVolunteerActivity() {
        String[][] cols = {
            {"activity_type", "VARCHAR(30) DEFAULT '综合'"},
            {"cover_image", "VARCHAR(500)"},
            {"contact_phone", "VARCHAR(20)"},
            {"difficulty", "TINYINT DEFAULT 1"},
            {"family_id", "BIGINT"},
            {"status", "TINYINT DEFAULT 1"}
        };
        String[] indexes = {
            "idx_family (family_id)",
            "idx_type (activity_type)",
            "idx_status (status)"
        };

        for (String[] col : cols) {
            try {
                jdbcTemplate.execute("ALTER TABLE volunteer_activity ADD COLUMN " + col[0] + " " + col[1]);
                log.info("  -> Column volunteer_activity.{} added", col[0]);
            } catch (Exception e) {
                log.debug("  -> Column volunteer_activity.{} already exists, skipped", col[0]);
            }
        }
        for (String idx : indexes) {
            try {
                jdbcTemplate.execute("ALTER TABLE volunteer_activity ADD INDEX " + idx);
                log.info("  -> Index volunteer_activity.{} added", idx);
            } catch (Exception e) {
                log.debug("  -> Index volunteer_activity.{} already exists, skipped", idx);
            }
        }
    }

    private void fixVolunteerRecord() {
        String[][] cols = {
            {"family_id", "BIGINT"},
            {"service_content", "TEXT"},
            {"photos", "VARCHAR(2000)"},
            {"beneficiary_feedback", "TEXT"},
            {"beneficiary_rating", "TINYINT"}
        };
        for (String[] col : cols) {
            try {
                jdbcTemplate.execute("ALTER TABLE volunteer_record ADD COLUMN " + col[0] + " " + col[1]);
                log.info("  -> Column volunteer_record.{} added", col[0]);
            } catch (Exception e) {
                log.debug("  -> Column volunteer_record.{} already exists, skipped", col[0]);
            }
        }
        try {
            jdbcTemplate.execute("ALTER TABLE volunteer_record ADD INDEX idx_family (family_id)");
            log.info("  -> Index volunteer_record.idx_family added");
        } catch (Exception e) {
            log.debug("  -> Index volunteer_record.idx_family already exists, skipped");
        }
    }

    private void fixJobPosition() {
        String[][] cols = {{"family_id", "BIGINT"}};
        for (String[] col : cols) {
            try { jdbcTemplate.execute("ALTER TABLE job_position ADD COLUMN " + col[0] + " " + col[1]); log.info("  -> Column job_position.{} added", col[0]); }
            catch (Exception e) { log.debug("  -> Column job_position.{} already exists, skipped", col[0]); }
        }
    }

    private void fixTrainingCourse() {
        String[][] cols = {{"family_id", "BIGINT"}};
        for (String[] col : cols) {
            try { jdbcTemplate.execute("ALTER TABLE training_course ADD COLUMN " + col[0] + " " + col[1]); log.info("  -> Column training_course.{} added", col[0]); }
            catch (Exception e) { log.debug("  -> Column training_course.{} already exists, skipped", col[0]); }
        }
    }

    private void fixNeedsPublish() {
        String[][] cols = {{"volunteer_id", "BIGINT"}, {"volunteer_confirm_time", "DATETIME"}};
        for (String[] col : cols) {
            try { jdbcTemplate.execute("ALTER TABLE needs_publish ADD COLUMN " + col[0] + " " + col[1]); log.info("  -> Column needs_publish.{} added", col[0]); }
            catch (Exception e) { log.debug("  -> Column needs_publish.{} already exists, skipped", col[0]); }
        }
        try { jdbcTemplate.execute("ALTER TABLE needs_publish ADD INDEX idx_volunteer_needs (volunteer_id)"); } catch (Exception e) {}
    }

    private void fixPovertyFamily() {
        String[][] cols = {{"latitude", "DOUBLE"}, {"longitude", "DOUBLE"}};
        for (String[] col : cols) {
            try { jdbcTemplate.execute("ALTER TABLE poverty_family ADD COLUMN " + col[0] + " " + col[1]); log.info("  -> Column poverty_family.{} added", col[0]); }
            catch (Exception e) { log.debug("  -> Column poverty_family.{} already exists, skipped", col[0]); }
        }
    }

    private void fixVisitRecord() {
        try { jdbcTemplate.execute("ALTER TABLE visit_record ADD COLUMN volunteer_user_id BIGINT"); log.info("  -> Column visit_record.volunteer_user_id added"); }
        catch (Exception e) { log.debug("  -> Column visit_record.volunteer_user_id already exists, skipped"); }
    }

    private void fixDonationGoods() {
        try { jdbcTemplate.execute("ALTER TABLE donation_goods ADD COLUMN distributed_by BIGINT"); log.info("  -> Column donation_goods.distributed_by added"); }
        catch (Exception e) { log.debug("  -> Column donation_goods.distributed_by already exists, skipped"); }
        try { jdbcTemplate.execute("ALTER TABLE donation_goods ADD INDEX idx_distributor (distributed_by)"); } catch (Exception e) {}
    }
}
