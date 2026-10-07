ALTER TABLE orders DROP FOREIGN KEY fk_orders_sales_link;
ALTER TABLE orders DROP FOREIGN KEY fk_orders_referred_by;
ALTER TABLE orders DROP INDEX idx_orders_sales_link;
ALTER TABLE orders DROP INDEX idx_orders_referred_by;
ALTER TABLE orders DROP COLUMN sales_link_id;
ALTER TABLE orders DROP COLUMN referred_by_user_id;

DROP TABLE employee_sales_links;
