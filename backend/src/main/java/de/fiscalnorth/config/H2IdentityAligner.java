package de.fiscalnorth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo {@code import.sql} inserts explicit primary keys. H2 IDENTITY then
 * restarts at 1, so the first user-created row collides (e.g. adding a holding).
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class H2IdentityAligner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        String url = environment.getProperty("spring.datasource.url", "");
        if (!url.startsWith("jdbc:h2")) {
            return;
        }
        align();
    }

    public void align() {
        List<String> tables = jdbcTemplate.queryForList(
                """
                SELECT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = 'PUBLIC'
                  AND COLUMN_NAME = 'ID'
                  AND IS_IDENTITY = 'YES'
                """,
                String.class);
        for (String table : tables) {
            String quoted = '"' + table.replace("\"", "") + '"';
            try {
                Long max = jdbcTemplate.queryForObject(
                        "SELECT COALESCE(MAX(id), 0) FROM " + quoted, Long.class);
                long next = (max == null ? 0L : max) + 1L;
                jdbcTemplate.execute(
                        "ALTER TABLE " + quoted + " ALTER COLUMN id RESTART WITH " + next);
            } catch (Exception ex) {
                log.warn("Could not align H2 identity for {}", table, ex);
            }
        }
        log.info("Aligned H2 IDENTITY columns for {} tables", tables.size());
    }
}
