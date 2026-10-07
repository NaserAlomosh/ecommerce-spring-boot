-- V19 originally shipped without this column in some development databases.
-- Reconcile those schemas without failing databases that applied the final V19.
SET @add_social_sales_link = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE website_settings ADD COLUMN social_sales_link VARCHAR(1000) NULL AFTER linkedin_url',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'website_settings'
      AND column_name = 'social_sales_link'
);

PREPARE add_social_sales_link_statement FROM @add_social_sales_link;
EXECUTE add_social_sales_link_statement;
DEALLOCATE PREPARE add_social_sales_link_statement;
