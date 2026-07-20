ALTER TABLE products
    ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'JOD' AFTER price;

UPDATE products
SET currency = 'JOD'
WHERE currency IS NULL OR currency = '';
