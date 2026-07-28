ALTER TABLE orders MODIFY COLUMN customer_id BIGINT NULL;
ALTER TABLE order_status_history MODIFY COLUMN changed_by_user_id BIGINT NULL;
