package com.smart.ecommerce.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("dev")
public class DevFlywayRecoveryConfig {
  private static final String EXPECTED_MISMATCH =
      "Migration checksum mismatch for migration version 19";
  private static final String APPLIED_CHECKSUM =
      "Applied to database : 1540734286";
  private static final String RESOLVED_CHECKSUM =
      "Resolved locally    : 240456515";

  @Bean
  @ConditionalOnProperty(
      name = "app.flyway.repair-v19-checksum",
      havingValue = "true")
  FlywayMigrationStrategy recoverV19Checksum() {
    return flyway -> {
      try {
        flyway.migrate();
      } catch (FlywayValidateException exception) {
        if (!isKnownV19Mismatch(exception) || !hasOriginalV19Schema(flyway))
          throw exception;
        log.warn("Repairing the known development-only V19 checksum mismatch; " +
                 "the original V19 schema was verified first");
        flyway.repair();
        flyway.migrate();
      }
    };
  }

  private boolean isKnownV19Mismatch(FlywayValidateException exception) {
    String message = exception.getMessage();
    return message != null && message.contains(EXPECTED_MISMATCH) &&
        message.contains(APPLIED_CHECKSUM) &&
        message.contains(RESOLVED_CHECKSUM);
  }

  private boolean hasOriginalV19Schema(org.flywaydb.core.Flyway flyway) {
    try (Connection connection =
             flyway.getConfiguration().getDataSource().getConnection()) {
      DatabaseMetaData metadata = connection.getMetaData();
      String catalog = connection.getCatalog();
      return column(metadata, catalog, "website_settings", "social_sales_link",
                    true);
    } catch (SQLException exception) {
      log.warn("Could not verify the original V19 schema before checksum recovery",
               exception);
      return false;
    }
  }

  private boolean column(DatabaseMetaData metadata, String catalog,
                         String table, String name, boolean nullable)
      throws SQLException {
    try (ResultSet columns =
             metadata.getColumns(catalog, null, table, name)) {
      if (!columns.next())
        return false;
      boolean actualNullable =
          columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
      return actualNullable == nullable;
    }
  }
}
