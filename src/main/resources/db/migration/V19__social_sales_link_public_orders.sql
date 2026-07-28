ALTER TABLE website_settings
    ADD COLUMN social_sales_link VARCHAR(1000) NULL AFTER linkedin_url;

ALTER TABLE orders MODIFY COLUMN customer_id BIGINT NULL;
ALTER TABLE order_status_history MODIFY COLUMN changed_by_user_id BIGINT NULL;
