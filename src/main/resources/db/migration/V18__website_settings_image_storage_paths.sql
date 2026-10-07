ALTER TABLE website_settings
    ADD COLUMN company_logo_storage_path VARCHAR(1000) AFTER company_logo_url,
    ADD COLUMN owner_image_storage_path VARCHAR(1000) AFTER owner_image_url;
