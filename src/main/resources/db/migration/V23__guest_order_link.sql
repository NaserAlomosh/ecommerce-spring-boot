ALTER TABLE website_settings
    ADD COLUMN guest_order_link TEXT NULL AFTER social_sales_link;

ALTER TABLE orders
    ADD COLUMN guest_order BOOLEAN NOT NULL DEFAULT FALSE AFTER customer_id;
