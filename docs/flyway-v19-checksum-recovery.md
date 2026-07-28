# Recovering a local Flyway V19 checksum mismatch

Flyway versioned migrations are immutable after they have been applied. If a development database ran an earlier draft of `V19__social_sales_link_public_orders.sql`, startup normally stops with an `Applied to database` / `Resolved locally` checksum mismatch. The `dev` profile recognizes the one known V19 checksum pair, verifies the complete schema described below, and repairs that checksum automatically. Any different mismatch or incomplete schema still fails startup instead of hiding a migration problem. Set `FLYWAY_REPAIR_V19_CHECKSUM=false` to disable this recovery.

## 1. Verify the schema before repairing

Run these queries against the affected database:

```sql
SELECT version, description, checksum, success
FROM flyway_schema_history
WHERE version = '19';

SELECT column_name, is_nullable, data_type
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND (
    (table_name = 'website_settings' AND column_name = 'social_sales_link')
    OR (table_name = 'orders' AND column_name = 'customer_id')
    OR (
      table_name = 'order_status_history'
      AND column_name = 'changed_by_user_id'
    )
  );
```

Repair is safe only when V19 has `success = 1` and the result contains all three columns with these properties:

| Table | Column | Expected |
|---|---|---|
| `website_settings` | `social_sales_link` | `varchar`, nullable |
| `orders` | `customer_id` | nullable |
| `order_status_history` | `changed_by_user_id` | nullable |

Do not manually update `flyway_schema_history.checksum` and do not disable Flyway validation.

## 2. Automatic development recovery

With the `dev` profile and the default `FLYWAY_REPAIR_V19_CHECKSUM=true`, restart the application. The recovery strategy verifies the three columns, repairs only the known applied/resolved V19 checksum pair, and then resumes migration. It is not active in production.

## 3. Manual repair when automatic recovery is disabled

Run Flyway repair once using the same connection settings as the application, and then restart the application:

```bash
mvn org.flywaydb:flyway-maven-plugin:10.10.0:repair \
  -Dflyway.url='jdbc:mysql://localhost:3306/ecommerce_dev' \
  -Dflyway.user='<database-user>' \
  -Dflyway.password='<database-password>'
```

`repair` updates Flyway's recorded checksum to the checked-in, resolved V19 checksum. It does not execute V19 again.

## 4. Recreate a disposable database whose schema is incomplete

If any expected column is missing or has the wrong nullability, do **not** repair the checksum. For a disposable local database, drop and recreate it, then let the application apply the checked-in migrations from an empty schema:

```sql
DROP DATABASE ecommerce_dev;
CREATE DATABASE ecommerce_dev
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

For a non-disposable/shared environment, first restore the exact V19 file that was originally deployed, then add the missing schema change as a new migration version. Never edit or replace a migration already applied to a shared environment.
