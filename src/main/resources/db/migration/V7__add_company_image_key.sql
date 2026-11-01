-- Companies can now have an image stored in the object storage; the column keeps the object key.
ALTER TABLE tb_company ADD COLUMN image_key TEXT;
